package com.zbutwialypiernik.flixage.util;

import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class RegexTest {

    @Test
    public void test_UUID_regex() {
        Pattern pattern = Pattern.compile(CommonRegex.UUID);

        Assertions.assertTrue(pattern.matcher("ddc642ca-1f3d-467a-8697-d99fb4fd91e8").matches());
        Assertions.assertFalse(pattern.matcher(" ddc642ca_1f3d_467a_8697_d99fb4fd91e8").matches());
        Assertions.assertFalse(pattern.matcher("ddc642ca_1f3d_467a_8697_d99fb4fd91e8 ").matches());
        Assertions.assertFalse(pattern.matcher("\nddc642ca_1f3d_467a_8697_d99fb4fd91e8").matches());
        Assertions.assertFalse(pattern.matcher("\tddc642ca_1f3d_467a_8697_d99fb4fd91e8").matches());
        // Bad divider
        Assertions.assertFalse(pattern.matcher("ddc642ca_1f3d_467a_8697_d99fb4fd91e8").matches());
        // Whitespace in section
        Assertions.assertFalse(pattern.matcher("ddc  2ca_1f3d-467a-8697-d99fb4fd91e8").matches());
        // Whitespace instead of "-"
        Assertions.assertFalse(pattern.matcher("ddc642ca 1f3d 467a 8697 d99fb4fd91e8").matches());
        // Non-alphanumeric char in section
        Assertions.assertFalse(pattern.matcher("ddc*22ca-1f3d-467a-8697-d99fb4fd91e8").matches());
    }

}
