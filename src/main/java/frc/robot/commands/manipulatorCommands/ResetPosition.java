package frc.robot.commands.manipulatorCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Manipulator;

public class ResetPosition extends Command
{
    private final Manipulator m_Manipulator;

    public ResetPosition(Manipulator M)
    {
        m_Manipulator = M;
        addRequirements(m_Manipulator);
    }
    @Override
    public void initialize(){
        m_Manipulator.moveArm(.3);
    }
    @Override
    public boolean isFinished(){
        return m_Manipulator.backSensor.get();
    }
    @Override
    public void end(boolean interrupted){
        m_Manipulator.moveArm(0);
    }
}
