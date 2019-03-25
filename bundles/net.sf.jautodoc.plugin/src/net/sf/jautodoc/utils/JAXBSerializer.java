/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Import and export via JAXB.
 */
public final class JAXBSerializer {

    private JAXBSerializer() {/* no instantiation */}

    @SuppressWarnings("unchecked")
    public static <T> T doImport(final String fileName, final Class<T> clazz) throws Exception {

        final JAXBContext context = JAXBContext.newInstance(clazz);
        final Unmarshaller unmarshaller = context.createUnmarshaller();

        Reader reader = null;
        try {
            reader = new FileReader(fileName);
            return (T)unmarshaller.unmarshal(reader);
        } finally {
            Utils.close(reader);
        }
    }

    public static <T> void doExport(final String fileName, final T jaxbElement) throws Exception {

        final JAXBContext context = JAXBContext.newInstance(jaxbElement.getClass());
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

        Writer writer = null;
        try {
            writer = new FileWriter(fileName);
            marshaller.marshal(jaxbElement, writer);
        } finally {
            Utils.close(writer);
        }
    }
}
