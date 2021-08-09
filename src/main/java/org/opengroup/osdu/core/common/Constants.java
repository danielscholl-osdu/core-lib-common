// Copyright 2017-2019, Schlumberger
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.opengroup.osdu.core.common;

public final class Constants {

    // Indexer parameters
    public static final String REINDEX_RELATIVE_URL = "/api/indexer/v2/_dps/task-handlers/reindex-worker";
    public static final String WORKER_RELATIVE_URL = "/api/indexer/v2/_dps/task-handlers/index-worker";

    public static final String INDEXER_QUEUE_IDENTIFIER = "indexer-queue-osdu";

    public static final String CRS = "crs";
    public static final String TYPE = "type";
    public static final String KIND = "kind";
    public static final String BBOX = "bbox";
    public static final String META = "meta";
    public static final String DATA = "data";
    public static final String POINT = "Point";
    public static final String FIELDS = "fields";
    public static final String POINTS = "points";
    public static final String KEYWORD = "keyword";
    public static final String FEATURE = "Feature";
    public static final String POLYGON = "Polygon";
    public static final String GEOMETRY = "geometry";
    public static final String FEATURES = "features";
    public static final String GEOMETRIES = "geometries";
    public static final String PROPERTIES = "properties";
    public static final String MULTIPOINT = "MultiPoint";
    public static final String LINE_STRING = "LineString";
    public static final String COORDINATES = "coordinates";
    public static final String ANY_CRS_POINT = "AnyCrsPoint";
    public static final String MULTIPOLYGON = "MultiPolygon";
    public static final String PROPERTY_NAMES = "propertyNames";
    public static final String ANY_CRS_POLYGON = "AnyCrsPolygon";
    public static final String ANY_CRS_FEATURE = "AnyCrsFeature";
    public static final String MULTI_LINE_STRING = "MultiLineString";
    public static final String WGS84_COORDINATES = "Wgs84Coordinates";
    public static final String ANY_CRS_MULTIPOINT = "AnyCrsMultiPoint";
    public static final String FEATURE_COLLECTION = "FeatureCollection";
    public static final String ANY_CRS_LINE_STRING = "AnyCrsLineString";
    public static final String GEOMETRYCOLLECTION = "GeometryCollection";
    public static final String ANY_CRS_MULTIPOLYGON = "AnyCrsMultiPolygon";
    public static final String PERSISTABLE_REFERENCE = "persistableReference";
    public static final String AS_INGESTED_COORDINATES = "AsIngestedCoordinates";
    public static final String ANY_CRS_MULTILINE_STRING = "AnyCrsMultiLineString";
    public static final String PERSISTABLE_REFERENCE_CRS = "persistableReferenceCrs";
    public static final String ANY_CRS_FEATURE_COLLECTION = "AnyCrsFeatureCollection";
    public static final String ANY_CRS_GEOMETRY_COLLECTION = "AnyCrsGeometryCollection";
    public static final String PERSISTABLE_REFERENCE_UNIT_Z = "persistableReferenceUnitZ";

    //headers needed to call storage and get converted data
    public static final String SLB_FRAME_OF_REFERENCE_VALUE = "units=SI;crs=wgs84;elevation=msl;azimuth=true north;dates=utc;";
}