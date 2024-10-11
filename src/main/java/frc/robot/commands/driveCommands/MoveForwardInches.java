package frc.robot.commands.driveCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.*;

public class MoveForwardInches extends Command{
    private final DriveTrain drivetrain;
    private final Manipulator manipulator;
    private final double speed;
    private final double distance;
    private final boolean intake;
    private double travelledDistance = 0.0;

    public MoveForwardInches(DriveTrain dT, Manipulator M, double speed, double distance, boolean enableIntake){
        drivetrain = dT;
        manipulator = M;
        intake = enableIntake;
        this.speed = speed;
        this.distance = Math.abs(distance);
        addRequirements(drivetrain, manipulator);
    }

    @Override
    public void initialize(){
        drivetrain.leftEncoder.setPosition(0.0);
        drivetrain.rightEncoder.setPosition(0.0);
        travelledDistance = 0.0;
        // if (intake) {manipulator.runIntake(false, true); manipulator.intakePosition(5, true); }
    }

    @Override
    public void execute(){
        manipulator.intakePosition(5, true);
        travelledDistance = Math.abs(getEncoderAvg());

        //Drive forward until the distance has been travelled
        if (travelledDistance < distance) drivetrain.drive(-speed, 0);
    }

    @Override
    public boolean isFinished(){
        return travelledDistance >= distance;
    }
    @Override
    public void end(boolean interrupted){
        manipulator.runIntake(false, false);
        drivetrain.drive(0, 0);
    }

    //Helper Functions
    public double getEncoderAvg(){
        double rightDist = Math.abs(drivetrain.rightEncoder.getPosition() / 8.45 * 18);
        double leftDist = Math.abs(drivetrain.leftEncoder.getPosition() / 8.45 * 18);
        return (rightDist + leftDist) / 2;
    }
}
