package frc.robot.driveCommands;

import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.*;

public class MoveForwardTimed extends WaitCommand{
    private final DriveTrain drivetrain;

    public MoveForwardTimed(DriveTrain dT, double waitSeconds){
        super(waitSeconds);
        drivetrain = dT;
    }

    @Override
    public void initialize(){
        drivetrain.drive(.4, 0);
        super.initialize();
    }
    @Override
    public void end(boolean interrupted){
        drivetrain.drive(0, 0);
    }
}
