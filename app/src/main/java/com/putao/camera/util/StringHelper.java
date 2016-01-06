
package com.putao.camera.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Html;
import android.text.Spanned;

public class StringHelper {
    public static String checkNull(String str) {
        if (isEmpty(str) || str.equals("null")) {
            str = "";
            return str;
        }
        return str;
    }

    /**
     * String convert to InputStream
     *
     * @param str
     * @return
     */
    public static InputStream stringToInputStream(String str) {
        try {
            return new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkEmailFormat(String email) {
        return checkRegex(email,
                "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
    }

    public static boolean checkMobileFormat(String mobile) {
        return checkRegex(mobile, "^[1][3,4,5,7,8][0-9]{9}$");
    }

    public static boolean checkNickNameFormat(String name) {
        if (name == null) {
            return false;
        }
        if (name.trim().length() < 1 || name.trim().length() > 16) {
            return false;
        }
        return true;
    }

    public static boolean checkPasswordFormat(String name) {
        return checkRegex(name, "[A-Z0-9a-z]{6,16}");
    }

    public static boolean checkRegex(final String text, final String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    /**
     * �?查是否是身高和体重的正确格式 �?0123456789.�?
     *
     * @param text
     * @return
     */
    public static boolean checkHWeightFormat(String text) {
        return checkRegex(text, "^[.0-9]+$");
    }

    /**
     * �?查指定的字符串是否为空�??
     * <ul>
     * <li>SysUtils.isEmpty(null) = true</li>
     * <li>SysUtils.isEmpty("") = true</li>
     * <li>SysUtils.isEmpty("   ") = true</li>
     * <li>SysUtils.isEmpty("abc") = false</li>
     * </ul>
     *
     * @param value 待检查的字符�?
     * @return true/false
     */
    public static boolean isEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0 || value.equalsIgnoreCase("Null") || value.equalsIgnoreCase("[]")) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * �?查对象是否为数字型字符串�?
     */
    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        }
        String str = obj.toString();
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        if (StringHelper.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern != null ? pattern.matcher(str) : null;
        if (isNum != null && !isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * �?查指定的字符串列表是否不为空�?
     */
    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    /**
     * 把�?�用字符编码的字符串转化为汉字编码�??
     */
    public static String unicodeToChinese(String unicode) {
        StringBuilder out = new StringBuilder();
        if (!isEmpty(unicode)) {
            for (int i = 0; i < unicode.length(); i++) {
                out.append(unicode.charAt(i));
            }
        }
        return out.toString();
    }

    /**
     * 过滤不可见字�?
     */
    public static String stripNonValidXMLCharacters(String input) {
        if (input == null || ("".equals(input)))
            return "";
        StringBuilder out = new StringBuilder();
        char current;
        for (int i = 0; i < input.length(); i++) {
            current = input.charAt(i);
            if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD)) || ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }

    /**
     * 字符串转为int
     *
     * @param text
     * @return
     */
    public static int stringToInt(String text) {
        try {
            int port = Integer.parseInt(text);
            return port;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * 字符串转为long
     *
     * @param text
     * @return
     */
    public static Long stringToLong(String text) {
        try {
            Long port = Long.parseLong(text);
            return port;
        } catch (NumberFormatException nfe) {
            return 0L;
        }
    }

    /**
     * 字符串转为double
     *
     * @param text
     * @return
     */
    public static Double stringToDouble(String text) {
        try {
            Double port = Double.parseDouble(text);
            return port;
        } catch (NumberFormatException nfe) {
            return 0d;
        }
    }

    public static String makeHtmlStr(String paramString, int paramInt) {
        return makeHtmlStr(paramString, String.valueOf(paramInt));
    }

    public static String makeHtmlStr(String paramString1, String paramString2) {
        return "<font color=\"" + paramString2 + "\">" + paramString1 + "</font>";
    }

    public static boolean checkNickname(String sequence) {
        final String format = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w-_]";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(sequence);
        return !matcher.find();
    }

    /**
     * 检测空就返回 ""
     *
     * @param str
     * @return
     */
    public static String checknull(String str) {
        return StringHelper.isEmpty(str) ? "" : str;
    }

    public static boolean checkChinese(String sequence) {
        final String format = "[\\u4E00-\\u9FA5\\uF900-\\uFA2D]";
        boolean result = false;
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(sequence);
        result = matcher.find();
        return result;
    }


    public static String decode(String content) {
        if (content == null)
            return "";
        String html = content;
        html = html.replaceAll("&amp;", "&");
        html = html.replace("&quot;", "\""); // "
        html = html.replace("&nbsp;&nbsp;", "\t");// 替换跳格
        html = html.replace("&nbsp;", " ");// 替换空格
        html = html.replace("&lt;", "<");
        html = html.replaceAll("&gt;", ">");
        return html;
    }

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }
        return md5StrBuff.toString().toLowerCase();
    }

    public static Spanned subZeroAndDot(String price) {
        String s = "";
        if (price.indexOf(".") > 0) {
            String[] str = price.split("\\.");
            Pattern pattern = Pattern.compile("[0+]*");
            Matcher isNum = pattern != null ? pattern.matcher(str[1]) : null;
            if (isNum != null && isNum.matches()) {
                price = price.replaceAll("0+?$", "");// 去掉多余�?0
                price = price.replaceAll("[.]$", "");// 如最后一位是.则去�?
                s = String.format("<font color='#ff841e'>�?%s</font>", price);
            } else {
                s = String.format("<font color='#ff841e'>�?%.2f</font>", Double.valueOf(price));
            }
        }
        return Html.fromHtml(s);
    }

    public static String getFormatPrice(String price) {
        String s = "";
        if (price.indexOf(".") > 0) {
            String[] str = price.split("\\.");
            Pattern pattern = Pattern.compile("[0+]*");
            Matcher isNum = pattern != null ? pattern.matcher(str[1]) : null;
            if (isNum != null && isNum.matches()) {
                price = price.replaceAll("0+?$", "");// 去掉多余�?0
                price = price.replaceAll("[.]$", "");// 如最后一位是.则去�?
                s = price;
            } else {
                s = String.format("%.2f", Double.valueOf(price));
            }
        }
        return s;
    }

    /**
     * @param price
     * @param color
     * @param args参数顺序决定了你想要的顺
     * @return
     */
    public static Spanned getFormatColorText(String color, String... args) {
        StringBuilder htmlStringBuilder = new StringBuilder();
        int length = args.length;
        htmlStringBuilder.append("<font color=").append(color).append(">");
        for (int i = 0; i < length; i++) {
            htmlStringBuilder.append(args[i]);
        }
        htmlStringBuilder.append("</font>");
        return Html.fromHtml(htmlStringBuilder.toString());
    }
}
