package edu.uiuc.ras;

import java.io.IOException;

/**
 * This class provides an interface to execute any linux shell commands.
 * @author adarshms
 */
public class ShellCommandExecutor
{
	// This is the command string to be executed.
	private String commandString;

	/**
	 * Constructor that accepts the command string.
	 * @param commandString
	 */
	public ShellCommandExecutor(String commandString)
	{
		this.commandString = commandString;
	}

	/**
	 * Method that executes the linux shell command (commandString).
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public int execute() throws IOException, InterruptedException
	{
		Process process = Runtime.getRuntime().exec(commandString);
		int exitValue = process.waitFor();
		return exitValue;
	}
}
