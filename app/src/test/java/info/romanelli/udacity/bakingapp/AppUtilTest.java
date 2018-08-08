package info.romanelli.udacity.bakingapp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class AppUtilTest {

    @Test
    public void testGetContentTypeInfo() {

        try {

            String url = null;
            boolean[] flags = AppUtil.getContentTypeInfo(url);
            assertFalse(flags[0]);
            assertFalse(flags[1]);
            assertFalse(flags[2]);
            assertFalse(flags[3]);
            assertFalse(flags[4]);

            url = "";
            flags = AppUtil.getContentTypeInfo(url);
            assertFalse(flags[0]);
            assertFalse(flags[1]);
            assertFalse(flags[2]);
            assertFalse(flags[3]);
            assertFalse(flags[4]);

            url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4";
            flags = AppUtil.getContentTypeInfo(url);
            assertTrue(flags[0]);
            assertFalse(flags[1]);
            assertFalse(flags[2]);
            assertFalse(flags[3]);
            assertFalse(flags[4]);

            url = "https://www.sample-videos.com/audio/mp3/crowd-cheering.mp3";
            flags = AppUtil.getContentTypeInfo(url);
            assertFalse(flags[0]);
            assertTrue(flags[1]);
            assertFalse(flags[2]);
            assertFalse(flags[3]);
            assertFalse(flags[4]);

            url = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";
            flags = AppUtil.getContentTypeInfo(url);
            assertFalse(flags[0]);
            assertFalse(flags[1]);
            assertTrue(flags[2]);
            assertFalse(flags[3]);
            assertFalse(flags[4]);

            url = "http://txt2html.sourceforge.net/sample.txt";
            flags = AppUtil.getContentTypeInfo(url);
            assertFalse(flags[0]);
            assertFalse(flags[1]);
            assertFalse(flags[2]);
            assertTrue(flags[3]);
            assertFalse(flags[4]);

//        url = "application"; // What's the url for an application content type?
//        flags = AppUtil.getContentTypeInfo(url);
//        assertFalse(flags[0]);
//        assertFalse(flags[1]);
//        assertFalse(flags[2]);
//        assertFalse(flags[3]);
//        assertTrue(flags[4]);

        } catch (Exception e) {
            Assert.fail(e.getLocalizedMessage());
        }

    }

}