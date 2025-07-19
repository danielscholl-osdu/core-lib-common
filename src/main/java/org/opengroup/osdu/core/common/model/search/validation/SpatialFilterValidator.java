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

package org.opengroup.osdu.core.common.model.search.validation;

import org.opengroup.osdu.core.common.SwaggerDoc;
import org.opengroup.osdu.core.common.model.search.Point;
import org.opengroup.osdu.core.common.model.search.Polygon;
import org.opengroup.osdu.core.common.model.search.SpatialFilter;
import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpatialFilterValidator implements ConstraintValidator<ValidSpatialFilter, SpatialFilter> {

    private static final long LONGITUDE_MAX_STANDARD = 180;
    private static final long LONGITUDE_MIN_STANDARD = -180;
    private static final long LONGITUDE_MAX_EXTENDED = 360;
    private static final long LONGITUDE_MIN_EXTENDED = -360;

    /**
     *  enabledExtendedRangeForLongitude
     *  should be set to true if you want let the Point longitude value be in range between -360 and 360.
     *  Default range is between -180 and 180
     */
    @Value("${validation.spatial.longitude.enableExtendedRange:false}")
    private boolean enabledExtendedRangeForLongitude;

    @Override
    public void initialize(ValidSpatialFilter validSpatialFilter) {

    }

    @Override
    public boolean isValid(SpatialFilter spatialFilter, ConstraintValidatorContext context) {

        // spatial filter can be null
        if (spatialFilter == null) {
            return true;
        }

        // only one of the spatial criteria should be used
        if ((spatialFilter.getByBoundingBox() != null && spatialFilter.getByDistance() != null) ||
                (spatialFilter.getByBoundingBox() != null && spatialFilter.getByGeoPolygon() != null) ||
                (spatialFilter.getByDistance() != null && spatialFilter.getByGeoPolygon() != null)) {
            return getViolation(context, "only one criteria can be used with SpatialFilter");
        }

        // validate bounding box
        if (spatialFilter.getByBoundingBox() != null) {
            double top = spatialFilter.getByBoundingBox().getTopLeft().getLatitude();
            double left = spatialFilter.getByBoundingBox().getTopLeft().getLongitude();
            double bottom = spatialFilter.getByBoundingBox().getBottomRight().getLatitude();
            double right = spatialFilter.getByBoundingBox().getBottomRight().getLongitude();

            if (!applyPointLongitudeValidation(Arrays.asList(spatialFilter.getByBoundingBox().getTopLeft(),
                    spatialFilter.getByBoundingBox().getBottomRight()), context)) {
                return false;
            }

            if (top < bottom) {
                return getViolation(context, String.format("top corner is below bottom corner: %s vs. %s", top, bottom));
            } else if (left > right) {
                return getViolation(context, String.format("left corner and right corner are flipped: %s vs. %s", left, right));
            } else if (top == bottom) {
                return getViolation(context, String.format("top latitude cannot be the same as bottom latitude: %s == %s", top, bottom));
            } else if (left == right) {
                return getViolation(context, String.format("left longitude cannot be the same as right longitude: %s == %s", left, right));
            }
        }

        if (spatialFilter.getByGeoPolygon() != null) {
            List<Point> points = spatialFilter.getByGeoPolygon().getPoints();
            if (!applyPointLongitudeValidation(points, context)) {
                return false;
            }
            Point start = points.get(0);
            if (start.equals(points.get(points.size() - 1))) {
                if (points.size() < 4) {
                    return getViolation(context, "too few points defined for geo polygon query");
                }
            } else {
                if (points.size() < 3) {
                    return getViolation(context, "too few points defined for geo polygon query");
                }
            }
        }

        if (spatialFilter.getByDistance() != null) {
            if(!applyPointLongitudeValidation(Collections.singletonList(spatialFilter.getByDistance().getPoint()), context)) {
                return false;
            }
            double distance = spatialFilter.getByDistance().getDistance();
            if (distance <= 0.0) {
                    return getViolation(context, "'distance' must be greater than 0");
            }
        }

        if (spatialFilter.getByIntersection() != null) {
            List<Polygon> polygons = Optional.ofNullable(spatialFilter.getByIntersection().getPolygons())
                    .orElse(Collections.emptyList());
            if(!applyPointLongitudeValidation(polygons.stream()
                    .flatMap(polygon -> polygon.getPoints().stream())
                    .collect(Collectors.toList()), context)){
                return false;
            }
        }

        if(spatialFilter.getByWithinPolygon() != null) {
            if (!applyPointLongitudeValidation(spatialFilter.getByWithinPolygon().getPoints(), context)) {
                return false;
            }
        }
        return true;
    }

    private boolean applyPointLongitudeValidation(List<Point> points, ConstraintValidatorContext context) {
        if (enabledExtendedRangeForLongitude) {
            return applyExtendedLongitudeValidation(points, context);
        } else {
            return applyStandardLongitudeValidation(points, context);
        }
    }

    private boolean applyStandardLongitudeValidation(List<Point> points, ConstraintValidatorContext context) {
        if (points != null) {
            for (Point point : points) {
                if (point != null) {
                    if (point.getLongitude() > LONGITUDE_MAX_STANDARD ||
                            point.getLongitude() < LONGITUDE_MIN_STANDARD) {
                        return getViolation(context, SwaggerDoc.LONGITUDE_VALIDATION_RANGE_MSG);
                    }
                }
            }
        }
        return true;
    }

    private boolean applyExtendedLongitudeValidation(List<Point> points, ConstraintValidatorContext context) {
        if (points != null) {
            for (Point point : points) {
                if (point != null) {
                    if (point.getLongitude() > LONGITUDE_MAX_EXTENDED ||
                            point.getLongitude() < LONGITUDE_MIN_EXTENDED) {
                        return getViolation(context, SwaggerDoc.LONGITUDE_VALIDATION_EXTENDED_RANGE_MSG);
                    }
                }
            }
        }
        return true;
    }

    private boolean getViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}