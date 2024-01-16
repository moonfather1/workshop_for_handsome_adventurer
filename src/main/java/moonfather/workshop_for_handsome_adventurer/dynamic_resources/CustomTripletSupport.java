package moonfather.workshop_for_handsome_adventurer.dynamic_resources;

public class CustomTripletSupport
{
    public static String stripPrefix(String wood)
    {
        return wood.startsWith("sx_") ? wood.substring(3) : wood;
    }

    public static String addPrefixTo(String id)
    {
        return "sx_" + id;
    }

    public static boolean isSpecial(String id)
    {
        return id.startsWith("sx_");
    }
}
