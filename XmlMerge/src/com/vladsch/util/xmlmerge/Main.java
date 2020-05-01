package com.vladsch.util.xmlmerge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        int i = 0;
        HashMap<String, String> replacedFiles = new HashMap<>();
        String version = null;

        if (args.length < 2) {
            printHelp();
        }

whileLabel:
        while (i < args.length) {
            switch (args[i]) {
                case "-s":
                    i++;

                    if (i >= args.length) {
                        System.out.println("-s option requires file-name argument");
                        printHelp();
                    }

                    replacedFiles.put(args[i++], "");
                    break;

                case "-r":
                    i++;

                    if (i + 1 >= args.length) {
                        System.out.println("-r option requires two file-name arguments");
                        printHelp();
                    }

                    String fromFile = args[i++];
                    String toFile = args[i++];
                    replacedFiles.put(fromFile, toFile);
                    break;

                default:
                    if (args[i].startsWith("-v")) {
                        version = args[i].substring(2);
                        i++;
                        break;
                    } else if (args[i].startsWith("-")) {
                        System.out.println(String.format("option '%s' is not recognized", args[i]));
                        printHelp();
                    }
                    break whileLabel;
            }
        }

        File inFile = new File(args[i++]);
        File outFile;
        if (i >= args.length) {
            String file = inFile.getAbsolutePath();
            int pos = file.lastIndexOf(".");
            if (pos >= 0) {
                file = file.substring(0, pos) + ".s" + file.substring(pos);
            } else {
                file = file + ".m";
            }

            outFile = new File(file);
        } else {
            outFile = new File(args[i++]);
        }

        if (i < args.length) {
            System.out.printf("Command line argument '%s' ignored\n", args[i]);
            printHelp();
        }

        try {
            FileReader fileReader = new FileReader(inFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder xml = new StringBuilder();
            String line;
            Pattern pattern = Pattern.compile("\\s*\\<xi\\:include\\s+href\\=\"([^\"]+)\"\\s+(?:xpointer\\=\"xpointer\\(([^\"]+)\\)\"\\s*)[^>]*/>\\s*");
            boolean lastWasBlankLine = true;

            while ((line = bufferedReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String href = matcher.group(1);
                    String tag = "";
                    if (matcher.groupCount() > 1) {
                        tag = matcher.group(2);
                        if (tag != null && tag.startsWith("/") && tag.endsWith("/*")) {
                            tag = tag.substring(1, tag.length() - 2);
                        } else {
                            tag = "";
                        }
                    }

                    if (tag.isEmpty()) {
                        tag = "idea-plugin";
                    }

                    String useHref = href;
                    if (replacedFiles.containsKey(href)) {
                        useHref = replacedFiles.get(href);
                    }

                    File includeFile = new File(inFile.getParentFile(), useHref);
                    if (!useHref.isEmpty() && includeFile.isFile() && includeFile.exists()) {
                        FileReader incReader = new FileReader(includeFile);
                        BufferedReader incBufferedReader = new BufferedReader(incReader);
                        String incLine;
                        boolean haveStart = false;
                        boolean firstLine = true;

                        while ((incLine = incBufferedReader.readLine()) != null) {
                            if (incLine.trim().equals("<" + tag + ">")) {
                                haveStart = true;
                            } else if (incLine.trim().equals("</" + tag + ">")) {
                                haveStart = false;
                            } else {
                                if (haveStart) {
                                    if (firstLine) {
                                        if (!lastWasBlankLine) xml.append("\n");
                                        xml.append("    <!-- ").append("Included from: ").append(href).append(" -->\n");
                                        firstLine = false;
                                        lastWasBlankLine = false;
                                    }

                                    boolean isBlank = incLine.trim().isEmpty();
                                    if (!lastWasBlankLine || !isBlank) {
                                        if (version != null) {
                                            incLine = incLine.replace("${version}", version);
                                        }
                                        xml.append(incLine);
                                        xml.append("\n");
                                    }
                                    lastWasBlankLine = isBlank;
                                }
                            }
                        }

                        incBufferedReader.close();
                    } else if (!useHref.isEmpty()) {
                        throw new IllegalArgumentException(String.format("Include href: %s not found relative to %s", href, inFile.getParentFile().getAbsolutePath()));
                    }
                } else {
                    boolean isBlank = line.trim().isEmpty();

                    if (!lastWasBlankLine || !isBlank) {
                        if (version != null) {
                            line = line.replace("${version}", version);
                        }
                        xml.append(line);
                        xml.append("\n");
                    }

                    lastWasBlankLine = isBlank;
                }
            }

            bufferedReader.close();

            FileWriter fileWriter = new FileWriter(outFile);
            fileWriter.append(xml.toString());
            fileWriter.append('\n');
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println("Usage: {-s suppressedFile} {-r replacedFile toFile} xmlmerge inFile {outFile}");
        System.out.println("       if no outFile is given then inFile will be used with '.m' prefixed to the extension");
        System.out.println("  -s   suppress include tag whose file name matched provided string from output");
        System.out.println("  -r   replace include file name");
        System.exit(1);
    }
}
