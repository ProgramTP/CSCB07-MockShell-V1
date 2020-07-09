package commands;

/**
 * Class Echo is responsible for printing to the shell and redirecting the input to perform the
 * appropriate task
 */
public class Echo extends FileManager implements CommandI {

  /** Declare instance variable of String to hold the arguments */
  private String argument;
  private int numArrows;

  /** Declare instance variable of String to hold the output that will be returned */
  String output = "";

  /**
   * Main run method that checks if the user had inputted any arguments to the command. It splices the
   * input so that it can send parsed data to the appropriate implementation of echo. Calls the
   * execute method that will perform the required task for the appropriate scenario. Returns a String
   * that will either contain an error message if there was an error or return null
   * 
   * @param args the String array of arguments provided by user (split from a whitespace)
   * @return String will either be null if there were no errors or an appropriate error message
   */
  public String run(String[] args, String fullInput, boolean val) {
    // If no arguments were inputted
    if (args.length == 0) {
      return getErrorHandler().getError("No parameters provided", "");
    }
    // Slices the input from whitespaces
    String[] slicedInput = fullInput.split(" ");

    // Parses the args array and assigns it to argument
    argument = fix_argument(slicedInput);
    // Execute the needed task
    execute(slicedInput, fullInput);
    // Return the output
    return output;
  }

  private boolean hasQuotations(String fullInput) {
    if (fullInput.contains("\"")) {
      int num = fullInput.length() - fullInput.replaceAll("\"", "").length();
      if (num % 2 == 0)
        return true;
    }
    return false;
  }

  private int count_arrows(String parsedInput) {
    return parsedInput.length() - parsedInput.replaceAll(">", "").length();
  }

  private String fix_argument(String[] splicedInput) {
    String args = "";
    // Loops through the args array and appends everything to argument
    for (int i = 1; i < splicedInput.length; i++) {
      args += splicedInput[i] + " ";
    }
    // Returns the parsed argument with the last whitespace removed
    return args.substring(0, args.length() - 1);
  }

  /**
   * Method that is responsible for printing the required arguments to the shell/console.
   * 
   * @param args the string array of arguments
   */
  public void printToConsole(String[] args) {
    for (int i = 1; i < args.length; i++) {
      output += args[i] + " ";
    }

    output = output.substring(0, output.length() - 1).replaceAll("\"", "");
  }

  private void executeAppend(String fileContents, String fileName,
      String fullInput) {
    // If user has inputted both file content and file name
    if (argument.split(">").length == 2 && !argument.split(">")[0].equals("")) {
      // Create the EchoOverwrite object
      EchoOverwrite overwriteExe = new EchoOverwrite();
      // Execute the overwriting command
      overwriteExe.execute(fileContents, fileName);
      output = null;
      return;
    } else
      // Throw an error
      output = this.getErrorHandler().getError("Invalid File", fullInput);
  }

  private void executeOverwrite(String fileContents, String fileName,
      String fullInput) {
    // If user has inputted both file content and file name
    if (argument.split(">>").length == 2
        && !argument.split(">>")[0].equals("")) {
      // Create the EchoAppend object
      EchoAppend appendExe = new EchoAppend();
      // Execute the overwriting command
      appendExe.execute(fileContents, fileName);
      output = null;
      return;
    } else
      // Throw an error
      output = this.getErrorHandler().getError("Invalid File", fullInput);
  }

  private void execute(String[] args, String fullInput) {
    String fileContents = "";
    // Checks for quotations in the input
    if (hasQuotations(fullInput)) {
      // Grabs the new file contents and file name
      fileContents = fullInput.substring(fullInput.indexOf("\"") + 1,
          fullInput.lastIndexOf("\""));
      String fileName =
          argument.substring(argument.lastIndexOf(">") + 1, argument.length());
      fileName = fileName.replaceAll("^\\s+", "");
      // Gets number of arrows to distinguish between append and overwrite
      numArrows = count_arrows(fullInput
          .substring(fullInput.lastIndexOf("\"") + 1, fullInput.length()));
      // Simple print to shell case
      if (numArrows == 0 && args.length > 1)
        printToConsole(args);
      // Overwrite case
      else if (numArrows == 1) {
        executeAppend(fileContents, fileName, fullInput);
        return;
      }
      // Append case
      else if (numArrows == 2) {
        executeOverwrite(fileContents, fileName, fullInput);
        return;
      } else
        // Error handling for arguments
        output = this.getErrorHandler().getError("Invalid Argument", fullInput);
      return;
    } else
      // Missing quotations in user input
      output = this.getErrorHandler().getError("Missing Quotes", fullInput);
    return;
  }
}
