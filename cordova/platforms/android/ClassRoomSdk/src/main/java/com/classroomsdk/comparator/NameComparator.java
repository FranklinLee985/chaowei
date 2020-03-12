package com.classroomsdk.comparator;

import com.classroomsdk.bean.ShareDoc;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.Comparator;

/**
 * Created by Administrator on 2018/5/7/007.
 */

public class NameComparator implements Comparator<ShareDoc> {

    private boolean isUp = true;

    public void setisUp(boolean isUp) {
        this.isUp = isUp;
    }

    @Override
    public int compare(ShareDoc o1, ShareDoc o2) {
        String str1 = getPingYin(o1.getFilename());
        String str2 = getPingYin(o2.getFilename());
        if(isUp){
            return str1.compareTo(str2);
        }else {
            return str2.compareTo(str1);
        }
    }

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param inputString
     * @return
     */
    public String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();// 把字符串转化成字符数组
        String output = "";

        try {
            for (int i = 0; i < input.length; i++) {
                // \\u4E00是unicode编码，判断是不是中文
                if (Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    // 将汉语拼音的全拼存到temp数组
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                            input[i], format);
                    // 取拼音的第一个读音
                    output += temp[0];
                }
                // 大写字母转化成小写字母
                else if (input[i] > 'A' && input[i] < 'Z') {
                    output += Character.toString(input[i]);
                    output = output.toLowerCase();
                }
                output += Character.toString(input[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
