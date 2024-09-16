// Written by Arnav Garg, garg0141 and Hayden Chu, chu00161

import java.util.Scanner;

/*
 * Provided in this class is the neccessary code to get started with your game's implementation
 * You will find a while loop that should take your minefield's gameOver() method as its conditional
 * Then you will prompt the user with input and manipulate the data as before in project 2
 * 
 * Things to Note:
 * 1. Think back to project 1 when we asked our user to give a shape. In this project we will be asking the user to provide a mode. Then create a minefield accordingly
 * 2. You must implement a way to check if we are playing in debug mode or not.
 * 3. When working inside your while loop think about what happens each turn. We get input, user our methods, check their return values. repeat.
 * 4. Once while loop is complete figure out how to determine if the user won or lost. Print appropriate statement.
 */

public class main {
	
	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in);
		
		boolean validInput = false;

		int choice = 2;
		int debug = 0;

		while(!validInput) { // loops until user inputs desired game difficulty
			try {
				System.out.print("Enter difficulty:");
				System.out.print(Minefield.ANSI_GREEN+"\n1: easy");
				System.out.print(Minefield.ANSI_YELLOW+"\n2: medium");
				System.out.print(Minefield.ANSI_RED+"\n3: hard");
				System.out.print(Minefield.ANSI_GREY_BACKGROUND+"\n");
				
				choice = Integer.parseInt(input.nextLine());

				System.out.println();
				if(choice < 1 || choice > 3) {
					System.out.println("Invalid input");
				}
				else {
					validInput = true;
				}
			} catch(NumberFormatException e) {
				System.out.println();
				System.out.println("Invalid input");
			}
		}
		validInput = false;

		while(!validInput) { // loops until user inputs debug mode choice
			try {
				System.out.print("Debug mode?");
				System.out.print(Minefield.ANSI_GREEN+"\n1: Yes");
				System.out.print(Minefield.ANSI_RED+"\n0: No");
				System.out.print(Minefield.ANSI_GREY_BACKGROUND+"\n");

				debug = Integer.parseInt(input.nextLine());

				System.out.println();
				if(debug < 0 || debug > 1) {
					System.out.println("Invalid input");
				}
				else {
					validInput = true;
				}
			} catch(NumberFormatException e) {
				System.out.println();
				System.out.println("Invalid input");
			}
		}
		validInput = false;
		
		int rows;
		int cols;
		int mines;
		
		switch(choice) { // determines minefield size based on user input
			case 1:
				rows = 5;
				cols = 5;
				mines = 5;
				break;
			case 2:
				rows = 9;
				cols = 9;
				mines = 12;
				break;
			case 3:
				rows = 20;
				cols = 20;
				mines = 40;
				break;
			default:
				rows = 9;
				cols = 9;
				mines = 12;
				break;
				
		}
		
		boolean isDebug = debug != 0;

		Minefield minefield = new Minefield(rows, cols, mines, isDebug); // creates new minefield

		System.out.println(minefield.toString());
		
	    while(!minefield.gameOver()){
	    	int[] processedAnswer = null;

	    	while(!validInput) {

		    	System.out.println("Enter a coordinate and if you wish to place a flag (Remaining flags: " + minefield.getFlags() + "): [x] [y] [1 for flag, 0 for no flag]");
		    	char[] answer = input.nextLine().toCharArray();
		    	System.out.println();
		    	
		    	processedAnswer = processAnswer(answer);
		    	
		    	if(processedAnswer == null) {
		    		System.out.println("Invalid input");
		    	}
		    	else {
		    		validInput = true;
		    	}

	    	}
	    	validInput = false;

	    	int x = processedAnswer[0];
	    	int y = processedAnswer[1];
	    	int flag = processedAnswer[2];
	    	
	    	boolean isFlag = flag != 0;
	    	if(minefield.guess(x, y, isFlag)){
				System.out.println(minefield.toString());
			} else {
				System.out.println("Try again");
			}

			if(minefield.gameOver()){
				System.out.println("Game over");
			}

	    }





		// determines whether game is won or lost
		if(minefield.getMines() == 0){
			System.out.println("You Win!");
		}else {
			System.out.println("You lost :(");
		}
	}



	private static int[] processAnswer(char[] answer) { // turns user input to usable array

		int x = 0;
		int y = 0;
		int flag = 0;
		
		// the number of spaces occured, needed to keep track of what number it's on
		int spaces = 0;
		
		// the index of last space, used to track the 10 power of current digit
		int lastSpaceIdx = answer.length;
		
		// builds numbers for all values by parsing through array
		for(int i = answer.length-1; i >= 0; i--) {
			
			// if current is space, increase space count
			if(answer[i] == ' ') {
				spaces++;
				lastSpaceIdx = i-1;
			}
			// check to make sure the current character is a number
			else if(Character.getNumericValue(answer[i]) > 9 || Character.getNumericValue(answer[i]) < 0) {
				return null;
			}
			else if(spaces == 0) {
				flag += Character.getNumericValue(answer[i]) * Math.pow(10, (lastSpaceIdx - i));
			}
			else if (spaces == 1) {
				x += Character.getNumericValue(answer[i]) * Math.pow(10, (lastSpaceIdx - i));
			}
			else if(spaces == 2) {
				y += Character.getNumericValue(answer[i]) * Math.pow(10, (lastSpaceIdx - i));
			}
		}
		int[] result = {x, y, flag};
		
		return result;
	}
}
