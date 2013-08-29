package eplus.handlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.discovery.ASMDataTable;
import eplus.EnchantingPlus;
import eplus.api.EplusPlugin;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
public class PluginHandler
{
    private static List<Class<EplusPlugin>> plugins = new ArrayList<Class<EplusPlugin>>();

    private static String getClassDisplayName(Class<EplusPlugin> clazz)
    {
        return clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1);
    }

    public static void init(Set<ASMDataTable.ASMData> all)
    {
        if (all != null && !all.isEmpty())
        {
            for (final ASMDataTable.ASMData data : all)
            {

                String className = data.getClassName();

                try
                {
                    @SuppressWarnings("unchecked")
                    final Class<EplusPlugin> clazz = (Class<EplusPlugin>) Class.forName(className);
                    clazz.getAnnotation(EplusPlugin.class);
                    className = getClassDisplayName(clazz);

                    plugins.add(clazz);
                    EnchantingPlus.log.info("Plugin loaded: " + className);

                } catch (final Exception e)
                {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    public static void initPlugins(LoaderState.ModState modState)
    {
        for (final Class<EplusPlugin> clazz : plugins)
        {
            final String className = getClassDisplayName(clazz);

            try
            {
                if (modState == LoaderState.ModState.POSTINITIALIZED && postInit(clazz))
                {
                    EnchantingPlus.log.info("Plugin initialized in PostInit... " + className);
                } else if (modState == LoaderState.ModState.PREINITIALIZED && preInit(clazz))
                {
                    EnchantingPlus.log.info("Plugin initialized in PreInit... " + className);
                }
            } catch (final Exception ex)
            {
                EnchantingPlus.log.info("Plugin failed to initialize.");
                EnchantingPlus.log.log(Level.INFO, "Reason for failure: {0}", ex.getLocalizedMessage());
            }
        }

    }

    private static boolean postInit(Class<EplusPlugin> clazz)
    {
        boolean successful = false;
        final Method[] methods = clazz.getDeclaredMethods();
        if (methods == null)
        {
            return false;
        }

        for (final Method method : methods)
        {
            if (method != null && method.isAnnotationPresent(EplusPlugin.PostInit.class))
            {
                try
                {
                    method.invoke(clazz.newInstance());
                    successful = true;
                } catch (final Exception e)
                {
                    continue;
                }
            }
        }
        return successful;
    }

    private static boolean preInit(Class<EplusPlugin> clazz)
    {
        boolean successful = false;
        final Method[] methods = clazz.getDeclaredMethods();
        if (methods == null)
        {
            return false;
        }

        for (final Method method : methods)
        {
            if (method != null && method.isAnnotationPresent(EplusPlugin.PreInit.class))
            {
                try
                {
                    method.invoke(clazz.newInstance());
                    successful = true;
                } catch (final Exception e)
                {
                    continue;
                }
            }
        }
        return successful;
    }
}
