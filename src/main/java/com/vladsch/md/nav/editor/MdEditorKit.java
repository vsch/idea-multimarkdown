// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.editor;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.ImageUtil;
import com.intellij.util.ui.UIUtil;
import com.vladsch.flexmark.util.sequence.SequenceUtils;
import com.vladsch.md.nav.psi.element.MdImageMultiLineUrlContentImpl;
import com.vladsch.md.nav.settings.MdApplicationSettings;
import com.vladsch.md.nav.settings.MdProjectSettings;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.settings.ProjectSettingsChangedListener;
import com.vladsch.md.nav.util.PathInfo;
import com.vladsch.plugin.util.image.ImageUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.vladsch.plugin.util.image.ImageUtils.isEncodedImage;

/**
 * {@link HTMLEditorKit} that can display images with paths relative to the document.
 *
 * @author Roger Grantham (https://github.com/grantham)
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @author Vladimir Schneider <vladimir.schneider@gmail.com>
 * @since 0.8
 */
public class MdEditorKit extends HTMLEditorKit implements Disposable {
    public static class ElementPosition {
        public final View view;
        public final int startOffset;
        public final int endOffset;
        public final int startElementOffset;
        public final int endElementOffset;

        public ElementPosition(
                View view,
                int startOffset,
                int endOffset,
                int startElementOffset,
                int endElementOffset
        ) {
            this.view = view;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.startElementOffset = startElementOffset;
            this.endElementOffset = endElementOffset;
        }

        @Override
        public String toString() {
            return "[" + view.getElement().getName() + " " + startOffset + ", " + endOffset + ", " + startElementOffset + ", " + endElementOffset + "]";
        }

        public int span() {
            return endOffset - startOffset;
        }

        public int elementSpan() {
            return endElementOffset - startElementOffset;
        }
    }

    /**
     * The document.
     */
    final protected Project myProject;
    private float maxWidth;
    private MessageBusConnection mySettingsConnection;
    @NotNull private final Disposable myDisposableParent;

    @NotNull
    public ArrayList<ElementPosition> getElementList() {
        return elementList;
    }

    ArrayList<ElementPosition> elementList = new ArrayList<ElementPosition>();

    public void clearElementList() {
        elementList.clear();
    }

    public void setMaxWidth(float maxWidth) { this.maxWidth = maxWidth; }

    public float getMaxWidth() { return maxWidth; }

    void updateMaxWidth(MdRenderingProfile settings) {
        double zoomFactor = settings.getPreviewSettings().getZoomFactor() * MdApplicationSettings.getInstance().getDocumentSettings().getZoomFactor();
        maxWidth = (float) Math.round(settings.getPreviewSettings().getMaxImageWidth() * zoomFactor);
    }

    /**
     * Build a new instance of {@link MdEditorKit}.
     */
    public MdEditorKit(@NotNull Project project, final @NotNull Disposable parent) {
        myProject = project;
        myDisposableParent = project;
        maxWidth = MdProjectSettings.getInstance(myProject).getPreviewSettings().getMaxImageWidth();

        Disposer.register(parent, this);

        mySettingsConnection = myProject.getMessageBus().connect();
        mySettingsConnection.subscribe(ProjectSettingsChangedListener.TOPIC, (project1, settings) -> {
            if (project1 == myProject) {
                updateMaxWidth(settings.getRenderingProfile());
            }
        });
    }

    /**
     * Creates a copy of the editor kit.
     *
     * @return a new {@link MdEditorKit} instance
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Object clone() {
        return new MdEditorKit(myProject, myDisposableParent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewFactory getViewFactory() {
        return new MarkdownViewFactory(this);
    }

    @Override
    public void dispose() {
        if (mySettingsConnection != null) {
            mySettingsConnection.disconnect();
            mySettingsConnection = null;
        }
        clearElementList();
    }

    /**
     * An {@link HTMLFactory} that uses {@link MarkdownImageView} for images.
     *
     * @author Roger Grantham (https://github.com/grantham)
     * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
     * @author Vladimir Schneider <vladimir.schneider@gmail.com>
     * @since 0.8
     */
    private static class MarkdownViewFactory extends HTMLFactory {
        private final MdEditorKit editorKit;

        MarkdownViewFactory(MdEditorKit editorKit) {
            this.editorKit = editorKit;
        }

        protected SimpleAttributeSet attributeSet;

        @Override
        public View create(Element elem) {
            View view = null;

            if (HTML.Tag.IMG.equals(elem.getAttributes().getAttribute(StyleConstants.NameAttribute))) {
                view = new MarkdownImageView(editorKit, elem);
            } else {
                view = super.create(elem);
            }

/*
            Object attribute = elem.getAttributes().getAttribute(MarkdownNavigatorExtension.SOURCE_POSITION_ATTRIBUTE_NAME);
            if (attribute instanceof String) {
                int pos = ((String) attribute).indexOf('-');
                if (pos > 0) {
                    try {
                        int start = Integer.parseInt(((String) attribute).substring(0, pos));
                        int end = Integer.parseInt(((String) attribute).substring(pos + 1));
                        int elemStart = elem.getStartOffset();
                        int elemEnd = elem.getEndOffset();
                        editorKit.elementList.add(new ElementPosition(view, start, end, elemStart, elemEnd));
                    } catch (Exception ignored) {
                    }
                }
            }
*/
            return view;
        }
    }

    /**
     * An {@link ImageView} that can resolve the image URL relative to the document.
     *
     * @author Roger Grantham (https://github.com/grantham)
     * @author Vladimir Schneider <vladimir.schneider@gmail.com>
     * @since 0.8
     */
    protected static class MarkdownImageView extends ImageView {
        private final MdEditorKit editorKit;
        private boolean scaled;
        private static HashMap<String, File> myImages = null;

        public static void clearCachedEmbeddedImages() {
            myImages = null;
        }

        MarkdownImageView(@NotNull MdEditorKit editorKit, @NotNull Element elem) {
            super(elem);
            this.editorKit = editorKit;

            scaled = false;
        }

        /**
         * Return a URL for the image source, or null if it could not be determined.
         * <p/>
         * Calls {@link javax.swing.text.html.ImageView#getImageURL()}, tries to resolve the relative if needed.
         *
         * @return a URL for the image source, or null if it could not be determined.
         */
        @Override
        public URL getImageURL() {
            //final String href = (String) getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
            //if (href.startsWith("file://")) {
            //    try {
            //        URL target = new URL(HelpersKt.prefixWith(HelpersKt.removeStart(href,"file://"), "file:/"));
            //        VirtualFileSystem virtualFileSystem = VirtualFileManager.getInstance().getFileSystem(target.getProtocol());
            //        String path = LinkRef.urlDecode(HelpersKt.removeStart(href, "file://")); // on windows it needs the stuff after file:// and target.getFile() does not have it
            //        final VirtualFile virtualFile = virtualFileSystem == null ? null : virtualFileSystem.findFileByPath(path);
            //        if (virtualFile != null && virtualFile.exists()) {
            //            URL url = new File(virtualFile.getPath()).toURI().toURL();
            //            return url;
            //        }
            //    } catch (MalformedURLException e) {
            //        e.printStackTrace();
            //    }
            //}
            //
            final String href = (String) getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
            // diagnostic: 1035, href can be null
            if (href != null && !href.isEmpty()) {
                BufferedImage image = null;

                if (myImages != null && MdApplicationSettings.getInstance().getDebugSettings().getCacheSvgForSwing()) {
                    if (myImages.containsKey(href)) {
                        final File embeddedImage = myImages.get(href);
                        try {
                            return embeddedImage.toURI().toURL();
                        } catch (MalformedURLException e) {
                            return super.getImageURL();
                        }
                    }
                }

                // not cached
                String fileName = "markdownNavigator_image";

                if (isEncodedImage(href)) {
                    image = ImageUtils.base64Decode(href);
                    fileName = "markdownNavigator_embeddedImage";
                }

                if (image == null) {
                    PathInfo url = new PathInfo(href);
                    // see if it is svg
                    String extWithDot = url.getExtWithDot();
                    boolean isSvgExt = extWithDot.equals(".svg");
                    int length = url.getProtocolURIPrefix() == null ? 0 : url.getProtocolURIPrefix().length();
                    boolean isGravizoCodeCogs = url.isURL() && (url.getFilePath().substring(length).startsWith(MdImageMultiLineUrlContentImpl.GRAVIZO_SVG_PREFIX_Q) ||
                            url.getFilePath().substring(length).startsWith(MdImageMultiLineUrlContentImpl.CODECOGS_SVG_PREFIX_Q));

                    // only try if not image extension or .svg extension, otherwise all images will be tried as svg
                    // and only if not absolute or is http: or file: protocol
                    if ((!url.isAbsolute() || (url.isURL() || url.isFileURI())) && (extWithDot.equals(".svg") || !url.isImageExt())) {
                        final String widthText = (String) getElement().getAttributes().getAttribute(HTML.Attribute.WIDTH);
                        final String heightText = (String) getElement().getAttributes().getAttribute(HTML.Attribute.HEIGHT);
                        final String classText = (String) getElement().getAttributes().getAttribute(HTML.Attribute.CLASS);
                        Integer width = SequenceUtils.parseUnsignedIntOrNull(widthText);
                        Integer height = SequenceUtils.parseUnsignedIntOrNull(heightText);

                        if (width == null && height != null) 
                            //noinspection SuspiciousNameCombination
                            width = height;
                        
                        if (height == null && width != null)
                            //noinspection SuspiciousNameCombination
                            height = width;

                        fileName = isSvgExt ? url.getFileName() : "markdownNavigator_svgImage";

                        boolean useTaskListImages = MdApplicationSettings.getInstance().getDebugSettings().getTaskItemImages();
                        boolean taskItemImage = "task-img".equals(classText) && useTaskListImages;

                        int scale = 1;

                        if (taskItemImage) {
                            scale = UIUtil.isJreHiDPI() ? 2 : 1;
                        }

                        image = width != null ? ImageUtils.loadSvgImageFromURLSized(href, width * scale, height * scale, false) : ImageUtils.loadSvgImageFromURL(href, 0f, false);

                        if (width != null && taskItemImage) {
                            int type = BufferedImage.TYPE_INT_ARGB;  // other options
                            BufferedImage dest = ImageUtil.createImage(width, height, type);
                            image = ImageUtils.overlayImage(dest, image, 0, 0);
                        }
                    }
                }

                if (image != null) {
                    try {
                        final File embeddedImage = File.createTempFile(fileName, ".png");
                        ImageUtils.save(image, embeddedImage, "PNG");
                        embeddedImage.deleteOnExit();

                        if (MdApplicationSettings.getInstance().getDebugSettings().getCacheSvgForSwing()) {
                            if (myImages == null) {
                                myImages = new HashMap<>();
                            }
                            myImages.put(href, embeddedImage);
                        }

                        try {
                            return embeddedImage.toURI().toURL();
                        } catch (MalformedURLException e) {
                            return super.getImageURL();
                        }
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }

            return super.getImageURL();
        }

        float width;
        float height;

        @Override
        public float getPreferredSpan(int axis) {
            if (!scaled) {
                float width = super.getPreferredSpan(View.X_AXIS);
                float height = super.getPreferredSpan(View.Y_AXIS);

                if (width < 0 || height < 0) return super.getPreferredSpan(axis);

                final float maxWidth = editorKit.getMaxWidth();

                if (maxWidth > 0 && width > maxWidth) {
                    scaled = true;
                    this.width = maxWidth;
                    this.height = (int) (height * maxWidth / width);

                    // force refresh of the image size
                    View parent = getParent();
                    super.setParent(null);
                    super.setParent(parent);
                } else {
                    this.width = super.getPreferredSpan(View.X_AXIS);
                    this.height = super.getPreferredSpan(View.Y_AXIS);
                }
            }
            return axis == View.X_AXIS ? this.width : (axis == View.Y_AXIS ? this.height : 0);
        }

        /**
         * Paints the View.
         *
         * @param g the rendering surface to use
         * @param a the allocated region to render into
         *
         * @see View#paint
         */
        @Override
        public void paint(@NotNull Graphics g, @NotNull Shape a) {
            float width = getPreferredSpan(View.X_AXIS);
            float height = getPreferredSpan(View.Y_AXIS);
            final float maxWidth = editorKit.getMaxWidth();

            if (maxWidth > 0 && width > maxWidth) {
                height = height * maxWidth / width;
                width = maxWidth;
            }

            Rectangle rect = (a instanceof Rectangle) ? (Rectangle) a :
                    a.getBounds();
            Rectangle clip = g.getClipBounds();

            if (clip != null) {
                g.clipRect(rect.x, rect.y,
                        rect.width,
                        rect.height);
            }

            Container host = getContainer();
            Image img = getImage();
            if (img != null) {
                if (width > 0 && height > 0) {
                    // Draw the image
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(img, rect.x, rect.y, (int) width, (int) height, null);
                }
            } else {
                Icon icon = getNoImageIcon();
                if (icon != null) {
                    icon.paintIcon(host, g,
                            rect.x, rect.y);
                }
            }
            if (clip != null) {
                // Reset clip.
                g.setClip(clip.x, clip.y, clip.width, clip.height);
            }
        }
    }
}
