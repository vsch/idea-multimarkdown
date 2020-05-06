/*
   Data extractor to Markdown Table
*/

function eachWithIdx(iterable, f) {
    var i = iterable.iterator();
    var idx = 0;
    while (i.hasNext()) f(i.next(), idx++);
}

function mapEach(iterable, f) {
    var vs = [];
    eachWithIdx(iterable, function (i) { vs.push(f(i));});
    return vs;
}

function escape(str) {
    str = str.replaceAll("\t|\b|\\f", "");
    // str = com.intellij.openapi.util.text.StringUtil.escapeXml(str);
    str = str.replaceAll("\\r|\\n|\\r\\n", "");
    str = str.replaceAll("([\\[\\]\\|])", "\\$1");
    return str;
}

var NEWLINE = "\n";

function output() { for (var i = 0; i < arguments.length; i++) { OUT.append(arguments[i]); } }

function outputRow(items) {
    output("| ");
    for (var i = 0; i < items.length; i++)
        output(escape(items[i]), " |");
    output("", NEWLINE);
}

if (TRANSPOSED) {
    var values = mapEach(COLUMNS, function (col) { return [col.name()]; });
    var heading = [];
    eachWithIdx(ROWS, function (row) {
        heading.push("---");
        eachWithIdx(COLUMNS, function (col, i) {
            values[i].push(FORMATTER.format(row, col));
        });
    });
    heading.push("---");
    outputRow(heading);
    eachWithIdx(COLUMNS, function (_, i) { outputRow(values[i]); });
}
else {
    outputRow(mapEach(COLUMNS, function (col) { return col.name(); }));
    outputRow(mapEach(COLUMNS, function (col) { return "---"; }));
    eachWithIdx(ROWS, function (row) {
        outputRow(mapEach(COLUMNS, function (col) { return FORMATTER.format(row, col); }));
    });
}

