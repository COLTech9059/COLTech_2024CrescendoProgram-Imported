package frc.robot;

public class Conversions {

    //The time it takes to do a full rotation THIS HAS NOT BEEN TESTED YET, TEMPORARY VALUE
    private static double rotationTime = 2.5;

    public static double findTurnTime(double desiredTurnAngle, double turnPower)
    {
        double rotationFactor = desiredTurnAngle/360;
        double returnTime = rotationTime/turnPower * rotationFactor;
        return returnTime;
    }
}
