package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.LimeLight;

public class LimeLightCommand extends Command{
    private final LimeLight limelight;
    private final DriveTrain drivetrain;
    
    public LimeLightCommand(LimeLight newLime, DriveTrain newDrive)
    {
        limelight = newLime;
        drivetrain = newDrive;
        addRequirements(limelight, drivetrain);
        limelight.start();
    }
    //# EXECUTE
    /* Overrides the execute function to constantly drive the robot given inputs.
     */
    @Override
    public void execute()
    {
        limelight.runLimelight(drivetrain);
    }

    @Override
    public boolean isFinished(){
        return limelight.getEnabled();
    }
}
