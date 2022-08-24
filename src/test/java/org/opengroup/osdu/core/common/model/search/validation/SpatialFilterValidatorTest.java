package org.opengroup.osdu.core.common.model.search.validation;

import org.mockito.Mockito;
import org.opengroup.osdu.core.common.model.search.Point;
import org.opengroup.osdu.core.common.model.search.SpatialFilter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SpatialFilterValidatorTest {

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
        Mockito.when(this.spatialFilter.getByGeoPolygon()).thenReturn(this.byGeoPolygon);
        assertFalse(sut.isValid(this.spatialFilter, this.constraintValidatorContext));

//        when(this.spatialFilter.getByDistance()).thenReturn(this.byDistance);
//        when(this.spatialFilter.getByGeoPolygon()).thenReturn(this.byGeoPolygon);
//        assertFalse(this.sut.isValid(this.spatialFilter, this.constraintValidatorContext));
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
