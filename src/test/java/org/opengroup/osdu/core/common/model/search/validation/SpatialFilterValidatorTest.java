package org.opengroup.osdu.core.common.model.search.validation;

import org.mockito.Mockito;
import org.opengroup.osdu.core.common.model.search.Point;
import org.opengroup.osdu.core.common.model.search.Polygon;
import org.opengroup.osdu.core.common.model.search.SpatialFilter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SpatialFilterValidatorTest {

    public static final String LONGITUDE_VALIDATION_RANGE_MSG = "'longitude' value is out of the range [-180, 180]";

    public static final String LONGITUDE_VALIDATION_EXTENDED_RANGE_MSG = "'longitude' value is out of the range [-360, 360]";

    private ConstraintValidatorContext constraintValidatorContext;

    @InjectMocks
    private SpatialFilterValidator sut;
    @Mock
    private SpatialFilter spatialFilter;
    @Mock
    private SpatialFilter.ByBoundingBox byBoundingBox;
    @Mock
    private SpatialFilter.ByDistance byDistance;
    @Mock
    private SpatialFilter.ByGeoPolygon byGeoPolygon;
    @Mock
    private SpatialFilter.ByIntersection byIntersection;
    @Mock
    private Polygon polygon;
    @Mock
    private SpatialFilter.ByWithinPolygon byWithinPolygon;

    @Before
    public void setup() {
        initMocks(this);

        this.constraintValidatorContext = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(this.constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_returnTrue_when_filterIsNull() {
        assertTrue(sut.isValid(null, null));
    }

    @Test
    public void should_allowOnlyOneSpatialFilter_when_called() {
        when(this.spatialFilter.getByBoundingBox()).thenReturn(this.byBoundingBox);
        when(this.spatialFilter.getByDistance()).thenReturn(this.byDistance);
        assertFalse(sut.isValid(this.spatialFilter, this.constraintValidatorContext));
    }

    @Test
    public void should_returnFalse_when_query_byBoundingBox_topBelowBottom() {
        setSpatialFilterValues(50.78, 60.00, 70.145, 70.23);
        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(this.constraintValidatorContext).buildConstraintViolationWithTemplate("top corner is below bottom corner: 50.78 vs. 70.145");
    }

    @Test
    public void should_returnFalse_when_query_byBoundingBox_leftIsRightBottom() {
        setSpatialFilterValues(80.78, 90.00, 70.145, 70.23);
        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(this.constraintValidatorContext).buildConstraintViolationWithTemplate("left corner and right corner are flipped: 90.0 vs. 70.23");
    }

    @Test
    public void should_returnFalse_when_query_byBoundingBox_leftEqualsRight() {
        setSpatialFilterValues(80.78, 100.00, 70.145, 100.00);
        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(this.constraintValidatorContext).buildConstraintViolationWithTemplate("left longitude cannot be the same as right longitude: 100.0 == 100.0");
    }

    @Test
    public void should_returnFalse_when_query_byBoundingBox_topEqualsBottom() {
        setSpatialFilterValues(50.78, 100.00, 50.78, 170.23);
        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(this.constraintValidatorContext).buildConstraintViolationWithTemplate("top latitude cannot be the same as bottom latitude: 50.78 == 50.78");
    }

    @Test
    public void should_returnFalse_when_query_byDistance_distanceEqualsZero() {
        setSpatialFilterDistanceValue(0.0);
        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(this.constraintValidatorContext).buildConstraintViolationWithTemplate("'distance' must be greater than 0");
    }

    @Test
    public void should_returnFalse_when_tooFewPoints_and_startEndNotSame() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(37.45, -122.1));
        points.add(new Point(38.45, -121.1));
        when(this.spatialFilter.getByGeoPolygon()).thenReturn(this.byGeoPolygon);
        when(this.spatialFilter.getByGeoPolygon().getPoints()).thenReturn(points);

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
    }

    @Test
    public void should_returnFalse_when_tooFewPoints_and_startEndSame() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(37.45, -122.1));
        points.add(new Point(38.45, -121.1));
        points.add(new Point(37.45, -122.1));
        when(this.spatialFilter.getByGeoPolygon()).thenReturn(this.byGeoPolygon);
        when(this.spatialFilter.getByGeoPolygon().getPoints()).thenReturn(points);

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
    }

    @Test
    public void test_constructor() {
        // for coverage purposes. Do nothing method!
        this.sut.initialize(null);
    }

    /**
     * Point longitude validation tests
     */

    // ByBoundingBox Point validation
    @Test
    public void shouldFailStandardPointLongitudeValidation_forGetByBoundingBox_withInvalidTopLeftLongitude() {
        Point topLeft = new Point(37.45, -181.1);
        Point bottomRight = new Point(37.45, -122.1);
        when(spatialFilter.getByBoundingBox()).thenReturn(byBoundingBox);
        when(byBoundingBox.getTopLeft()).thenReturn(topLeft);
        when(byBoundingBox.getBottomRight()).thenReturn(bottomRight);

        assertFalse(this.sut.isValid(spatialFilter, constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(LONGITUDE_VALIDATION_RANGE_MSG);
    }

    @Test
    public void shouldFailStandardPointLongitudeValidation_forGetByBoundingBox_withInvalidBottomRightLongitude() {
        Point topLeft = new Point(37.45, -120.1);
        Point bottomRight = new Point(37.45, -181.1);
        when(this.spatialFilter.getByBoundingBox()).thenReturn(byBoundingBox);
        when(byBoundingBox.getTopLeft()).thenReturn(topLeft);
        when(byBoundingBox.getBottomRight()).thenReturn(bottomRight);

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
    }

    @Test
    public void shouldPassExtendedPointLongitudeValidation_forGetByBoundingBox_withInvalidStandardValues() {
        enableExtendedRangeForLongitude();
        Point topLeft = new Point(38.45, 182.1);
        Point bottomRight = new Point(37.45, 183.1);
        when(this.spatialFilter.getByBoundingBox()).thenReturn(byBoundingBox);
        when(byBoundingBox.getTopLeft()).thenReturn(topLeft);
        when(byBoundingBox.getBottomRight()).thenReturn(bottomRight);

        assertTrue(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
    }

    @Test
    public void shouldFailExtendedPointLongitudeValidation_forGetByBoundingBox_withInvalidExtendedValues() {
        enableExtendedRangeForLongitude();
        Point topLeft = new Point(38.45, 361.2);
        Point bottomRight = new Point(37.45, 362.3);
        when(this.spatialFilter.getByBoundingBox()).thenReturn(byBoundingBox);
        when(byBoundingBox.getTopLeft()).thenReturn(topLeft);
        when(byBoundingBox.getBottomRight()).thenReturn(bottomRight);

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(LONGITUDE_VALIDATION_EXTENDED_RANGE_MSG);
    }

    // ByGeoPolygon Point validation
    @Test
    public void shouldFailStandardPointLongitudeValidation_forGetByGeoPolygon_withInvalidStandardValues() {
        Point point = new Point(38.45, 181.2);
        when(this.spatialFilter.getByGeoPolygon()).thenReturn(byGeoPolygon);
        when(byGeoPolygon.getPoints()).thenReturn(singletonList(point));

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(LONGITUDE_VALIDATION_RANGE_MSG);
    }

    @Test
    public void shouldPassExtendedPointLongitudeValidation_forGetByGeoPolygon_withInvalidStandardValues() {
        enableExtendedRangeForLongitude();
        Point point = new Point(38.45, 181.2);
        when(this.spatialFilter.getByGeoPolygon()).thenReturn(byGeoPolygon);
        when(byGeoPolygon.getPoints()).thenReturn(Arrays.asList(point, point, point, point));

        assertTrue(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
    }

    @Test
    public void shouldFailExtendedPointLongitudeValidation_forGetByGeoPolygon_withInvalidExtendedValues() {
        enableExtendedRangeForLongitude();
        Point point = new Point(38.45, 361.2);
        when(this.spatialFilter.getByGeoPolygon()).thenReturn(byGeoPolygon);
        when(byGeoPolygon.getPoints()).thenReturn(Arrays.asList(point, point, point, point));

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(LONGITUDE_VALIDATION_EXTENDED_RANGE_MSG);
    }

    // ByDistance Point validation
    @Test
    public void shouldFailStandardPointLongitudeValidation_forGetByDistance_withInvalidStandardValues() {
        Point point = new Point(38.45, 181.2);
        when(this.spatialFilter.getByDistance()).thenReturn(byDistance);
        when(byDistance.getPoint()).thenReturn(point);

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(LONGITUDE_VALIDATION_RANGE_MSG);
    }

    @Test
    public void shouldPassExtendedPointLongitudeValidation_forGetByDistance_withInvalidStandardValues() {
        enableExtendedRangeForLongitude();
        Point point = new Point(38.45, 181.2);
        when(this.spatialFilter.getByDistance()).thenReturn(byDistance);
        when(byDistance.getPoint()).thenReturn(point);
        when(byDistance.getDistance()).thenReturn(100.1);

        assertTrue(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
    }

    @Test
    public void shouldFailExtendedPointLongitudeValidation_forGetByDistance_withInvalidExtendedValues() {
        enableExtendedRangeForLongitude();
        Point point = new Point(38.45, 361.2);
        when(this.spatialFilter.getByDistance()).thenReturn(byDistance);
        when(byDistance.getPoint()).thenReturn(point);

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(LONGITUDE_VALIDATION_EXTENDED_RANGE_MSG);
    }

    // ByIntersection Point validation
    @Test
    public void shouldFailStandardPointLongitudeValidation_forGetByIntersection_withInvalidStandardValues() {
        Point point = new Point(38.45, 181.2);
        when(this.spatialFilter.getByIntersection()).thenReturn(byIntersection);
        when(byIntersection.getPolygons()).thenReturn(singletonList(polygon));
        when(polygon.getPoints()).thenReturn(singletonList(point));

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(LONGITUDE_VALIDATION_RANGE_MSG);
    }

    @Test
    public void shouldPassExtendedPointLongitudeValidation_forGetByIntersection_withInvalidStandardValues() {
        enableExtendedRangeForLongitude();
        Point point = new Point(38.45, 181.2);
        when(this.spatialFilter.getByIntersection()).thenReturn(byIntersection);
        when(byIntersection.getPolygons()).thenReturn(singletonList(polygon));
        when(polygon.getPoints()).thenReturn(singletonList(point));

        assertTrue(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
    }

    @Test
    public void shouldFailExtendedPointLongitudeValidation_forGetByIntersection_withInvalidExtendedValues() {
        enableExtendedRangeForLongitude();
        Point point = new Point(38.45, 361.2);
        when(this.spatialFilter.getByIntersection()).thenReturn(byIntersection);
        when(byIntersection.getPolygons()).thenReturn(singletonList(polygon));
        when(polygon.getPoints()).thenReturn(singletonList(point));

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(LONGITUDE_VALIDATION_EXTENDED_RANGE_MSG);
    }

    // ByWithinPolygon Point validation
    @Test
    public void shouldFailStandardPointLongitudeValidation_forGetByWithinPolygon_withInvalidStandardValues() {
        Point point = new Point(38.45, 181.2);
        when(this.spatialFilter.getByWithinPolygon()).thenReturn(byWithinPolygon);
        when(byWithinPolygon.getPoints()).thenReturn(singletonList(point));

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(LONGITUDE_VALIDATION_RANGE_MSG);
    }

    @Test
    public void shouldPassExtendedPointLongitudeValidation_forGetByWithinPolygon_withInvalidStandardValues() {
        enableExtendedRangeForLongitude();
        Point point = new Point(38.45, 181.2);
        when(this.spatialFilter.getByWithinPolygon()).thenReturn(byWithinPolygon);
        when(byWithinPolygon.getPoints()).thenReturn(singletonList(point));

        assertTrue(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
    }

    @Test
    public void shouldFailExtendedPointLongitudeValidation_forGetByWithinPolygon_withInvalidExtendedValues() {
        enableExtendedRangeForLongitude();
        Point point = new Point(38.45, 361.2);
        when(this.spatialFilter.getByWithinPolygon()).thenReturn(byWithinPolygon);
        when(byWithinPolygon.getPoints()).thenReturn(singletonList(point));

        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate(LONGITUDE_VALIDATION_EXTENDED_RANGE_MSG);
    }

    private void enableExtendedRangeForLongitude() {
        Field enabledExtendedRangeForLongitude =
                ReflectionUtils.findField(SpatialFilterValidator.class, "enabledExtendedRangeForLongitude");
        ReflectionUtils.makeAccessible(enabledExtendedRangeForLongitude);
        ReflectionUtils.setField(enabledExtendedRangeForLongitude, sut, true);
    }

    private void setSpatialFilterValues(double top, double left, double bottom, double right) {
        SpatialFilter.ByBoundingBox byBoundingBox = new SpatialFilter.ByBoundingBox();
        byBoundingBox.setTopLeft(new Point(top, left));
        byBoundingBox.setBottomRight(new Point(bottom, right));
        when(this.spatialFilter.getByBoundingBox()).thenReturn(byBoundingBox);
    }

    private void setSpatialFilterDistanceValue(double distance) {
        SpatialFilter.ByDistance byDistance = new SpatialFilter.ByDistance();
        byDistance.setDistance(distance);
        when(this.spatialFilter.getByDistance()).thenReturn(byDistance);
    }

}
