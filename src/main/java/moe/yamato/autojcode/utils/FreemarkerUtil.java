package moe.yamato.autojcode.utils;

import freemarker.template.*;

import java.io.IOException;

public abstract class FreemarkerUtil {

    private static final Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);

    static {
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        cfg.setClassForTemplateLoading(FreemarkerUtil.class, "/template");
    }

    public static Template getTemplate(String name) {
        try {
            return cfg.getTemplate(name);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
