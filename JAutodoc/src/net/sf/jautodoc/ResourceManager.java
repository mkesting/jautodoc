/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;


/**
 * Class for managing resources.
 */
@SuppressWarnings("restriction")
public final class ResourceManager {
    public static final String JAVADOC_IMAGE_FILE = "javadoc_template.gif";
    public static final String MATCH_DETAIL_IMAGE = "match_detail.gif";
    public static final String EXPORT_IMAGE_FILE = "exportpref_wiz.png";
    public static final String IMPORT_IMAGE_FILE = "importpref_wiz.png";

    public static final String DEFAULT   = "default";
    public static final String COMMENT   = "comment";
    public static final String DIRECTIVE = "directive";
    public static final String STRING    = "string";
    public static final String REFERENCE = "reference";
    public static final String OPERATOR  = "operator";
    public static final String NUMBER    = "number";
    public static final String RESULT    = "result";
    public static final String TAG       = "tag";
    public static final String MARKUP    = "markup";
    public static final String HEADING   = "heading";
    public static final String GROUP     = "group";
    public static final String NOMATCH   = "nomatch";
    public static final String NORMAL    = "normal";
    public static final String ESCAPES   = "escapes";

    private static final RGB RGB_DEFAULT   = new RGB( 64,  96, 192);
    private static final RGB RGB_COMMENT   = new RGB( 64, 128,  96);
    private static final RGB RGB_DIRECTIVE = new RGB(192, 128, 128);
    private static final RGB RGB_STRING    = new RGB(128,   0,   0);
    private static final RGB RGB_REFERENCE = new RGB(128, 128,   0);
    private static final RGB RGB_OPERATOR  = new RGB(  0,   0,   0);
    private static final RGB RGB_NUMBER    = new RGB(192,   0,   0);
    private static final RGB RGB_RESULT    = new RGB( 64, 192,  96);
    private static final RGB RGB_TAG       = new RGB(128, 160, 192);
    private static final RGB RGB_MARKUP    = new RGB(128, 128, 160);
    private static final RGB RGB_HEADING   = new RGB(  0,   0,   0);
    private static final RGB RGB_GROUP     = new RGB( 64, 192,  96);
    private static final RGB RGB_NOMATCH   = new RGB(192,  64,  96);
    private static final RGB RGB_NORMAL    = new RGB(  0,   0,   0);
    private static final RGB RGB_ESCAPES   = new RGB(128, 128, 160);


    private static JavaColorManager colorManager;

    private static Map<String, IToken> tokens;
    private static Map<String, TextAttribute> attributes;
    private static ImageRegistry imageRegistry;

    static {
        colorManager = new JavaColorManager();

        attributes = new HashMap<String, TextAttribute>();
        attributes.put(DEFAULT,   new TextAttribute(colorManager.getColor(RGB_DEFAULT)));
        attributes.put(COMMENT,   new TextAttribute(colorManager.getColor(RGB_COMMENT)));
        attributes.put(DIRECTIVE, new TextAttribute(colorManager.getColor(RGB_DIRECTIVE), null, SWT.BOLD));
        attributes.put(STRING,    new TextAttribute(colorManager.getColor(RGB_STRING)));
        attributes.put(REFERENCE, new TextAttribute(colorManager.getColor(RGB_REFERENCE)));
        attributes.put(OPERATOR,  new TextAttribute(colorManager.getColor(RGB_OPERATOR)));
        attributes.put(NUMBER,    new TextAttribute(colorManager.getColor(RGB_NUMBER)));
        attributes.put(RESULT,    new TextAttribute(colorManager.getColor(RGB_RESULT)));
        attributes.put(TAG,       new TextAttribute(colorManager.getColor(RGB_TAG), null, SWT.BOLD));
        attributes.put(MARKUP,    new TextAttribute(colorManager.getColor(RGB_MARKUP)));
        attributes.put(HEADING,   new TextAttribute(colorManager.getColor(RGB_HEADING), null, SWT.BOLD));
        attributes.put(GROUP,     new TextAttribute(colorManager.getColor(RGB_GROUP)));
        attributes.put(NOMATCH,   new TextAttribute(colorManager.getColor(RGB_NOMATCH)));
        attributes.put(NORMAL,    new TextAttribute(colorManager.getColor(RGB_NORMAL)));
        attributes.put(ESCAPES,   new TextAttribute(colorManager.getColor(RGB_ESCAPES)));

        tokens = new HashMap<String, IToken>();
        tokens.put(DEFAULT,   new Token(getTextAttribute(DEFAULT)));
        tokens.put(COMMENT,   new Token(getTextAttribute(COMMENT)));
        tokens.put(DIRECTIVE, new Token(getTextAttribute(DIRECTIVE)));
        tokens.put(STRING,    new Token(getTextAttribute(STRING)));
        tokens.put(REFERENCE, new Token(getTextAttribute(REFERENCE)));
        tokens.put(OPERATOR,  new Token(getTextAttribute(OPERATOR)));
        tokens.put(NUMBER,    new Token(getTextAttribute(NUMBER)));
        tokens.put(RESULT,    new Token(getTextAttribute(RESULT)));
        tokens.put(TAG,       new Token(getTextAttribute(TAG)));
        tokens.put(MARKUP,    new Token(getTextAttribute(MARKUP)));
        tokens.put(HEADING,   new Token(getTextAttribute(HEADING)));
        tokens.put(GROUP,     new Token(getTextAttribute(GROUP)));
        tokens.put(NOMATCH,   new Token(getTextAttribute(NOMATCH)));
        tokens.put(NORMAL,    new Token(getTextAttribute(NORMAL)));
        tokens.put(ESCAPES,   new Token(getTextAttribute(ESCAPES)));

        imageRegistry = new ImageRegistry();
    }


    /* no instantiation */
    private ResourceManager() {}

    /**
     * Gets the token with the given name.
     *
     * @param name the name
     *
     * @return the token
     */
    public static IToken getToken(String name) {
        IToken token = tokens.get(name);
        if (token == null) {
            token = tokens.get(DEFAULT);
        }
        return token;
    }

    /**
     * Gets the text attribute with the given name.
     *
     * @param name the name
     *
     * @return the text attribute
     */
    public static TextAttribute getTextAttribute(String name) {
        TextAttribute attribute = attributes.get(name);
        if (attribute == null) {
            attribute = attributes.get(DEFAULT);
        }
        return attribute;
    }

    /**
     * Gets the image descriptor from given image file.
     *
     * @param imageFile the image file
     *
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(final String imageFile) {
        final URL url = FileLocator.find(JAutodocPlugin.getDefault().getBundle(),
                new Path("icons/" + imageFile), null);
        return url == null ? null : ImageDescriptor.createFromURL(url);
    }

    /**
     * Gets the image from given image file.
     *
     * @param imageFile the image file
     *
     * @return the image
     */
    public static Image getImage(String imageFile) {
        Image image = imageRegistry.get(imageFile);
        if (image != null) {
            return image;
        }

        final ImageDescriptor imageDescriptor = getImageDescriptor(imageFile);
        if (imageDescriptor == null) {
            return null;
        }

        image = imageDescriptor.createImage();
        if (image != null) {
            imageRegistry.put(imageFile, image);
        }

        return image;
    }
}
