package com.wjwu.wpmain.util;

/**
 * Created by wjwu on 2015/9/4.
 */
public class HtmlTools {
//
//
//    public static String makeHtmls(String title, String body) {
//        return
//                "<!DOCTYPE html>\n" +
//                        "<html>\n" +
//                        "<head>\n" +
//                        "<meta charset=\"UTF-8\">\n" +
//                        "<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0;\">\n" +
//                        "\n" +
//                        "<title>" + title + "</title>\n" +
//                        "<style type=\"text/css\">\n" +
//                        "body {\n" +
//                        "\tpadding: 0px;\n" +
//                        "\tmargin: 0px;\n" +
//                        "\tfont-family: arial;\n" +
//                        "}\n" +
//                        "\n" +
//                        "p {\n" +
//                        "\tcolor: rgb(48, 48, 48);\n" +
//                        "\tdisplay: block;\n" +
//                        "    font-family: STHeitiSC-LightXi;\n" +
//                        "<!--\tfont-family: arial;-->\n" +
//                        "\tfont-size: 14px;\n" +
//                        "\tmargin-bottom: 24px;\n" +
//                        "    margin-left : 0px;\n" +
//                        "    margin-right:0px;\n" +
//                        "\tline-height: 24px;\n" +
//                        "    word-break:break-all;//强制英文换行\n" +
//                        "}\n" +
//                        "\n" +
//                        ".pcolor {\n" +
//                        "\tcolor: rgb(102, 102, 102);\n" +
//                        "}\n" +
//                        "\n" +
//                        "img {\n" +
//                        "\tdisplay: block;\n" +
//                        "\tmargin: 0 auto;\n" +
//                        "\tborder: 0;\n" +
//                        "\theight: auto;\n" +
//                        "\tmax-width: 100%;\n" +
//                        "\tvertical-align: middle;\n" +
//                        "}\n" +
//                        "\n" +
//                        "h3 {\n" +
//                        "\tclear: both;\n" +
//                        "\tcolor: rgb(51, 51, 51);\n" +
//                        "\tdisplay: block;\n" +
//                        "\tfont-family: arial;\n" +
//                        "\tfont-size: 18px;\n" +
//                        "\tfont-style: normal;\n" +
//                        "\tfont-weight: bold;\n" +
//                        "}\n" +
//                        "\n" +
//                        "h1 {\n" +
//                        "\tfont-size: 23px;\n" +
//                        "    font-family: STHeiti-Light;\n" +
//                        "\tcolor: rgb(48, 48, 48);\n" +
//                        "}\n" +
//                        "</style>\n" +
//                        "\n" +
//                        "</head>\n" +
//                        "<body>\n" +
//                        "<h1>" + title + "</h1>" +
//                        "\n" + body +
//                        "\t</body>\n" +
//                        "\t</html>";
//    }
//
//
//    public static String makeHtmlsNight(String title, String time, String body) {
//        return
//                "<!DOCTYPE html>\n" +
//                        "<html>\n" +
//                        "<head>\n" +
//                        "<meta charset=\"UTF-8\">\n" +
//                        "<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0;\">\n" +
//                        "\n" +
//                        "<title>" + title + "</title>\n" +
//                        "<style type=\"text/css\">\n" +
//                        "body {\n" +
//                        "\tpadding: 0px;\n" +
//                        "\tmargin: 0px;\n" +
//                        "\tfont-family: arial;\n" +
//                        "}\n" +
//                        "\n" +
//                        "p {\n" +
//                        "\tcolor: rgb(48, 48, 48);\n" +
//                        "\tdisplay: block;\n" +
//                        "    font-family: STHeitiSC-LightXi;\n" +
//                        "<!--\tfont-family: arial;-->\n" +
//                        "\tfont-size: 14px;\n" +
//                        "\tmargin-bottom: 24px;\n" +
//                        "    margin-left : 0px;\n" +
//                        "    margin-right:0px;\n" +
//                        "\tline-height: 24px;\n" +
//                        "    word-break:break-all;//强制英文换行\n" +
//                        "}\n" +
//                        "\n" +
//                        ".pcolor {\n" +
//                        "\tcolor: rgb(102, 102, 102);\n" +
//                        "}\n" +
//                        "\n" +
//                        "img {\n" +
//                        "\tdisplay: block;\n" +
//                        "\tmargin: 0 auto;\n" +
//                        "\tborder: 0;\n" +
//                        "\theight: auto;\n" +
//                        "\tmax-width: 100%;\n" +
//                        "\tvertical-align: middle;\n" +
//                        "}\n" +
//                        "\n" +
//                        "h3 {\n" +
//                        "\tclear: both;\n" +
//                        "\tcolor: rgb(51, 51, 51);\n" +
//                        "\tdisplay: block;\n" +
//                        "\tfont-family: arial;\n" +
//                        "\tfont-size: 18px;\n" +
//                        "\tfont-style: normal;\n" +
//                        "\tfont-weight: bold;\n" +
//                        "}\n" +
//                        "\n" +
//                        "h1 {\n" +
//                        "\tfont-size: 23px;\n" +
//                        "    font-family: STHeiti-Light;\n" +
//                        "\tcolor: rgb(48, 48, 48);\n" +
//                        "}\n" +
//                        "</style>\n" +
//                        "\n" +
//                        "</head>\n" +
//                        "<script language=\"javascript\">\n" +
//                        "    function load_theme(bgc, txtc){\n" +
//                        "\t\tdocument.getElementsByTagName('body')[0].style.webkitTextFillColor=txtc\n" +
//                        "       // document.bgColor=bgc\n" +
//                        "\t\tdocument.getElementsByTagName('body')[0].style.background=bgc\n" +
//                        "    }\n" +
//                        "</script>" +
//                        "<body>\n" +
//                        "<h1>" + title + "</h1>" +
//                        "<p>" + time + "</p>" +
//                        "\n" + body +
//                        "\t</body>\n" +
//                        "\t</html>";
//    }
}
