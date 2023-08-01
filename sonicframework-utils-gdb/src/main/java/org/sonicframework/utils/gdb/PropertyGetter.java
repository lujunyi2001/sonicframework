package org.sonicframework.utils.gdb;

import org.gdal.ogr.Feature;

/**
* @author lujunyi
*/
@FunctionalInterface
interface PropertyGetter {
    Object get(Feature feature, int index);
}
