/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for CamelCaseSplitter.
 */
public class TestCamelCaseSplitter {

    @Test
    public void testSplit() {
        String[] result = CamelCaseSplitter.split("");
        assertEquals(0, result.length);

        result = CamelCaseSplitter.split(null);
        assertEquals(0, result.length);

        result = CamelCaseSplitter.split("update");
        assertEquals(1, result.length);

        result = CamelCaseSplitter.split("updateForInvoice");
        assertEquals(3, result.length);
        assertArrayEquals(new String[] {"update", "For", "Invoice"}, result);

        result = CamelCaseSplitter.split("getID4Produkt");
        assertEquals(4, result.length);
        assertArrayEquals(new String[] {"get", "ID", "4", "Produkt"}, result);

        result = CamelCaseSplitter.split("update4Invoice");
        assertEquals(3, result.length);
        assertArrayEquals(new String[] {"update", "4", "Invoice"}, result);

        result = CamelCaseSplitter.split("update4invoice");
        assertEquals(3, result.length);
        assertArrayEquals(new String[] {"update", "4", "invoice"}, result);

        result = CamelCaseSplitter.split("update4IDOfInvoice");
        assertEquals(5, result.length);
        assertArrayEquals(new String[] {"update", "4", "ID", "Of", "Invoice"}, result);

        result = CamelCaseSplitter.split("update4ID12345Invoice");
        assertEquals(5, result.length);
        assertArrayEquals(new String[] {"update", "4", "ID", "12345", "Invoice"}, result);

        result = CamelCaseSplitter.split("update4ID12345Invoice56");
        assertEquals(6, result.length);
        assertArrayEquals(new String[] {"update", "4", "ID", "12345", "Invoice", "56"}, result);

        result = CamelCaseSplitter.split("update4ID12345Invoice56Foo");
        assertEquals(7, result.length);
        assertArrayEquals(new String[] {"update", "4", "ID", "12345", "Invoice", "56", "Foo"}, result);

        result = CamelCaseSplitter.split("update4ID12345Invoice56FXY");
        assertEquals(7, result.length);
        assertArrayEquals(new String[] {"update", "4", "ID", "12345", "Invoice", "56", "FXY"}, result);

        result = CamelCaseSplitter.split("update4ID12345Invoice56FXy");
        assertEquals(8, result.length);
        assertArrayEquals(new String[] {"update", "4", "ID", "12345", "Invoice", "56", "F", "Xy"}, result);

        result = CamelCaseSplitter.split("update4ID12345Invo ice56FXy");
        assertEquals(9, result.length);
        assertArrayEquals(new String[] {"update", "4", "ID", "12345", "Invo", "ice", "56", "F", "Xy"}, result);

        result = CamelCaseSplitter.split("update_for_invoice");
        assertEquals(3, result.length);
        assertArrayEquals(new String[] {"update", "for", "invoice"}, result);

        result = CamelCaseSplitter.split("update_4_invoice");
        assertEquals(3, result.length);
        assertArrayEquals(new String[] {"update", "4", "invoice"}, result);
    }
}
