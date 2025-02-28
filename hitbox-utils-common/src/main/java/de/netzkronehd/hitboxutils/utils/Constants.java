package de.netzkronehd.hitboxutils.utils;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static class PluginMessage {
        public static final String BUNGEE_CORD = "BungeeCord";
        public static final String PLUGIN_MESSAGE_CHANNEL = "hitboxutils:message_channel";
        public static final String PLAYTIME_REQUEST = "requestonlinetime";
        public static final String PLAYTIME_ANSWER = "onlinetime";
        public static final String STAFF_SETTINGS = "staffsettings";
        public static final String BUNGEE_COMMAND = "bungeecommand";

        public static final String SOUND = "playsound";
    }


    public static final String COMMAND_PREFIX = "hitbox";

    public static final String PERMISSION_PREFIX = "hitbox.";

    public static final String META_DATA_KEY = "hitboxutils_meta_data";
    public static final String META_DATA_KEY_SPECTATING = META_DATA_KEY+"_spectating";
    public static final String META_DATA_KEY_VANISHED = META_DATA_KEY + "_vanished";
    public static final String IPV4_PATTERN = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    public static final List<String> SPANISH_INSULTS = buildSpanishInsults();

    public static final List<String> GLOBAL_INSULTS = new ArrayList<>();

    static {
        GLOBAL_INSULTS.addAll(SPANISH_INSULTS);
    }

    private static List<String> buildSpanishInsults() {
        return List.of(
                "aborto", "anal", "culo",
                "estúpido", "bolsa de pelota", "bolas", "bastardo", "me cago en tu nación", "bufas",
                "anormal", "perra", "perras", "muerto de hambre", "sangriento",
                "mamada", "pasen porno", "teta", "tetas", "los pechos", "fracasado",
                "imbecil", "chupa poyas", "cipa", "clítoris",
                "polla", "chupar la polla", "subnormal", "mapache", "mierda",
                "corrida", "retrasao", "coño", "me cago en tus muertos",
                "consolador", "consoladores", "tonto", "perro follador", "duche",
                "mongolo", "eyacular", "eyaculado", "eyacula", "eyaculación", "maricón",
                "fagging", "maricones", "marica", "felación", "brida", "follada",
                "cabron", "folladores", "maldito", "carajo", "folla", "mariquita",
                "me cago en tus putos muertos pisoteados", "me cago en tus putisimos muertos", "hore", "córneo", "fracasao", "kock",
                "labios vaginales", "chupa pijas", "masoquista", "masturbarse",
                "madre folladora", "negro", "nigger", "bufarracas",
                "cabron", "pene", "mear", "retrasado", "pisser", "panchito",
                "pornografía", "pinchazo", "pinchazos",
                "pube", "coños", "violación", "violador",
                "recto", "fornicar", "rimming",
                "gilipoyas", "escroto", "pelusa", "follar",
                "cagadas", "cagado", "lame coños",
                "de mierda", "fantasma", "puta", "puta", "smegma",
                "tizón", "arrebatar", "espacio",
                "follar", "testículo", "pornhub",
                "hacerse una paja"
        );
    }
}
