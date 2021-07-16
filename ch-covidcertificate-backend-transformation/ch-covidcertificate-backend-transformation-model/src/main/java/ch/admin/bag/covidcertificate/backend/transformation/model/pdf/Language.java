package ch.admin.bag.covidcertificate.backend.transformation.model.pdf;

import java.util.Locale;

public enum Language {
    DE,
    FR,
    IT,
    RM;

    public static Language forLocale(Locale locale) {
        if (locale == null) {
            return getFallback();
        } else {
            String code = locale.getLanguage();
            for (Language language : Language.values()) {
                if (language.name().equalsIgnoreCase(code)) {
                    return language;
                }
            }
        }
        return getFallback();
    }

    public static Language getFallback() {
        return DE;
    }
}
