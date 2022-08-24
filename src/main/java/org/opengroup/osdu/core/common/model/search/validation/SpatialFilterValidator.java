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

import org.opengroup.osdu.core.common.model.search.Point;
import org.opengroup.osdu.core.common.model.search.SpatialFilter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class SpatialFilterValidator implements ConstraintValidator<ValidSpatialFilter, SpatialFilter> {

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
            double distance = spatialFilter.getByDistance().getDistance();
            if (distance <= 0.0) {
                    return getViolation(context, "'distance' must be greater than 0");
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