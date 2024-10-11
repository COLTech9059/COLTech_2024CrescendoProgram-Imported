package frc.robot.commands.driveCommands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Conversions;
import frc.robot.subsystems.DriveTrain;

public class TurnWithAngle extends Command
{
    private DriveTrain drivetrain;

    private Timer turnTimer = new Timer();

    private double turnTime;
    private double turnPower;

    private boolean finished;

    public TurnWithAngle(DriveTrain dT, double turnPower, double desiredAngle)
    {
        drivetrain = dT;
        this.turnPower = turnPower;
        this.turnTime = Conversions.findTurnTime(Math.abs(desiredAngle), turnPower);

        addRequirements(drivetrain);
    }

    @Override
    public void initialize()
    {
        turnTimer.start();
        drivetrain.drive(0, turnPower);
    }

    @Override
    public void execute()
    {
        if (turnTimer.get() >= turnTime) finished = true;
        else finished = false;
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }

    @Override
    public void end(boolean interrupted)
    {
        drivetrain.drive(0 ,0);
        turnTimer.reset();
        turnTimer.stop();
    }
}
