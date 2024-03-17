package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveTrain;

import java.util.function.DoubleSupplier;

public class DriveCommand extends Command
{
    
    private final DriveTrain drivetrain;

    private DoubleSupplier forward;
    private DoubleSupplier turn;
    
    public DriveCommand(DriveTrain dT, DoubleSupplier f, DoubleSupplier t)
    {
        drivetrain = dT;
        forward = f;
        turn = t;
        addRequirements(drivetrain);
    }
    //# EXECUTE
    /* Overrides the execute function to constantly drive the robot given inputs.
     */
    @Override
    public void execute()
    {
        drivetrain.drive(-forward.getAsDouble(), turn.getAsDouble());
    }

}

