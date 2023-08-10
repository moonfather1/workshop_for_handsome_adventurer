package moonfather.workshop_for_handsome_adventurer.initialization;

import java.util.HashMap;

public class ExternalWoodSupport
{
    private static final HashMap<String, String> woodToHostMap = new HashMap<>();
    private static final HashMap<String, String> woodToPrefixMap = new HashMap<>();

    public static String getHostMod(String wood) { return woodToHostMap.get(wood); }
    public static void registerHostMod(String wood, String hostMod) { woodToHostMap.put(wood, hostMod); }

    ////////////////////////////

    public static String getPrefix(String wood) { return woodToPrefixMap.getOrDefault(wood, ""); }
    public static void registerPrefix(String wood, String hostMod) { woodToPrefixMap.put(wood, hostMod); }
}
