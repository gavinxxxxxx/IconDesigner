package me.gavin.icon.designer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import me.gavin.util.Base64Helper;
import me.gavin.util.L;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("me.gavin.icon.designer", appContext.getPackageName());
    }

    @Test
    public void io() {
//        String str = "gzip的基础是DEFLATE，DEFLATE是LZ77与哈夫曼编码的一个组合体。DEFLATE最初是作为LZW以及其它受专利保护的数据压缩算法的替代版本而设计的，当时那些专利限制了compress以及其它一些流行的归档工具的应用。\n" +
//                "文件格式说明：\n" +
//                "10字节的头，包含幻数、版本号以及时间戳\n" +
//                "可选的扩展头，如原文件名\n" +
//                "文件体，包括DEFLATE压缩的数据\n" +
//                "8字节的尾注，包括CRC-32校验和以及未压缩的原始数据长度\n" +
//                "尽管这种文件格式允许多个这样的数据拼接在一起，在解压时也能认出它们是拼接在一起的数据，但通常gzip仅用来压缩单个文件。多个文件的压缩归档通常是首先将这些文件合并成一个tar文件，然后再使用gzip进行压缩，最后生成的.tar.gz或者.tgz文件就是所谓的“tar压缩包”或者“tarball”。\n" +
//                "注意不要将gzip和ZIP压缩格式混淆。ZIP也使用DEFLATE算法，而且可移植性更好，不需要一个外部的归档工具就可以包容多个文件。但是，由于ZIP对每个文件进行单独压缩而没有利用文件间的冗余信息（即固实压缩），所以ZIP的压缩率会稍逊于tar压缩包。\n" +
//                "zlib是DEFLATE算法的实现库，它的API同时支持gzip文件格式以及一个简化的数据流格式。zlib数据流格式、DEFLATE以及gzip文件格式均已被标准化成了，分别是RFC 1950、RFC 1951以及RFC 1952。";
        UUID str = UUID.randomUUID();
        L.e(str);
        String str1 = Base64Helper.toBase64(str);
        L.e(str1);
        Object str2 = Base64Helper.fromBase64(str1);
        L.e(str2);
    }
}
