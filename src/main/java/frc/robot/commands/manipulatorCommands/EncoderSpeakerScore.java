package frc.robot.commands.manipulatorCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Manipulator;

public class EncoderSpeakerScore extends Command
{

    private final Manipulator m_Manipulator;
    private boolean finished = false;
    private boolean didIPos = false;
    private boolean didSPos = false;
    private boolean didShoot = false;

    public EncoderSpeakerScore (Manipulator sentManip) 
    {
        m_Manipulator = sentManip;
        addRequirements(m_Manipulator);
    }

    @Override
    public void execute()
    {
        if (m_Manipulator.intakePosition(5, true) && !didIPos) didIPos = true;
        if (didIPos)
        {
            if (m_Manipulator.shootPosition(5, true) && !didSPos) didSPos = true;
            if (didSPos)
            {
                m_Manipulator.holdManipulator(true);
                if (m_Manipulator.shootNote(true) && !didShoot) didShoot = true;
                if (didShoot) finished = true;
            }
        }
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }

    @Override
    public void end(boolean interrupted)
    {
        finished = false;
        m_Manipulator.revUpFlywheel(false);
        m_Manipulator.runIntake(false, false);
        m_Manipulator.holdManipulator(false);
        m_Manipulator.resetEncoders();
    }
}
