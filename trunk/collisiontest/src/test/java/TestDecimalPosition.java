import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.common.MathHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 29.03.13
 * Time: 15:47
 */
public class TestDecimalPosition {
    @Test
    public void testAngel() {
        Assert.assertEquals(MathHelper.gradToRad(0), new DecimalPosition(0, -2).getAngleToNorth(), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(45), new DecimalPosition(-2, -2).getAngleToNorth(), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(90), new DecimalPosition(-2, 0).getAngleToNorth(), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(135), new DecimalPosition(-2, 2).getAngleToNorth(), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(180), new DecimalPosition(0, 2).getAngleToNorth(), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(225), new DecimalPosition(2, 2).getAngleToNorth(), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(270), new DecimalPosition(2, 0).getAngleToNorth(), 0.0001);
        Assert.assertEquals(MathHelper.gradToRad(-45), new DecimalPosition(2, -2).getAngleToNorth(), 0.0001);
    }
}
