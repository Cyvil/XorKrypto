
import java.io.*;

public class OneTimePad {

//	 private static String inputFileName = "2011044.xor";
	 private static String inputFileName = "C:\\Tempo\\2011044.xor"; //- lokalnie, na moim dysku :)))
//	 private static String outputFileName = "C:\\Tempo\\wyjscie.txt"; //-- nie uzywam jak na razie
	 
	 
	 //Funkcja sprawdz�jaca czy dana literka jest "dobra" - tj czy nalezy do polskiego alfabetu badz jest czestym znakiem interpunkcyjnym 
	 //Moze istnieje potrzeba dorobienia kolejnych znakow ale watpie
	 //te heksy to spacja, tab i polskie znaki w kodowaniu windows-1250
	 public static boolean goodByte(byte input)
	 {
		int i = (int) input;
	if ((i >= 'a' && i <= 'z') || (i >= 'A' && i <= 'Z') || (i >= '0' && i <= '9') || (i ==0x09) || (i ==0x20) || (i == ' ') || (i ==',') || (i =='?') || (i =='!') || (i == 0xa5) || (i == 0xb9) ||  (i == 0xea) || (i == 0xca) || (i == 0x8c) || (i == 0x9c) || (i == 0xc6) || (i == 0xe6) || (i == 0x8f) || (i == 0x9f) || (i == 0xaf) || (i == 0xbf) || (i == 0xd3) || (i == 0xf3) )
		return true;
		else return false;
		 
	 }
	 
	 //Funkcja sprawdzajaca czy cala kolumna jest "dobra" - czyli czy w kazdym bloku na miejscu oznczonym nr xor literki z kluczem daje cos "dobrego"
	 
	 public static boolean goodColumn(byte[][] input, byte key, int nr)
	 {
		 int tmpBlockSize = input.length - 1;
		
		 for (int i = 0; i < tmpBlockSize; i++)
		 {
			 int temp = input[i][nr] ^ key;
			 byte tempByte = (byte) temp;
			 if(!goodByte(tempByte))
				 return false;
				 
		 }
		 return true;
	 }
	
	 //Main :)))
	 
	public static void main(String[] args) throws IOException  {
		
		//Czytamy plik i dzielimy na bloki
		
		File file = new File(inputFileName);
		FileInputStream in = new FileInputStream(file);
	    long length = file.length();
	    long blockSize = length/256;

	    System.out.println("ilosc blokow: " + blockSize);
	    System.out.println("dlugosc pliku: " + length);

	    //Konstruujemy nasza tablice zakodowanych blokow

	    byte[][] encryptedBytes = new byte[(int) blockSize][256];
	    
	    //czytamy do tablicy bajt po bajcie
	    
	    for(int i = 0; i < (blockSize-1); i++)
	    {
	    	for(int j = 0; j < 256; j++)
	    	{
	 		encryptedBytes[i][j] = (byte)in.read();
	    	}	
	   }

	    //Inicjujemy pare dodatkowych tablic 
	   
	    byte[] keyBytes = new byte[256]; //klucz
	    byte[][] decryptedBytes = new byte[(int) blockSize][(int) length]; //tekst jawny
	    
	    byte[][] tempBytes = new byte[((int) blockSize) - 1][256]; //xory poszczegolnych par
//	    byte[] resultBytes = new byte[256]; //takie cos posrednie
	    boolean[] goodBytes = new boolean[256]; // tablica booleowska pilnujaca klucza - zeby w kolko nie nadpisywac tych samych pozycji
	    
	    //xorujemy j-ty blok z blokiem j+1-tym
	    
    	for(int j = 0; j < tempBytes.length-1;j++)
    	{
    		for(int i = 0; i < 256; i++)
    		{
    			goodBytes[i] = false;

    			int temp = encryptedBytes[j][i] ^ encryptedBytes[j+1][i];
    			tempBytes[j][i] = (byte) temp;
    		}	
    	}
    	
    	
    	
    	//Wsadzamy zera w klucz
    	for(int i = 0; i < 256; i++)
    	{
    		keyBytes[i] = 0;
    	}
 
    	//inicjalizujemy  literke, z ktora xorowac bedziemy bloki tempBytes
    	
    	char letter = 0;
      	//dla kazdych kolejnych xorowanych dwoch blokow

    	for (int z = 0; z < tempBytes.length; z++)
    	{	
    	
   			//dla kazdego mozliwego bajtu
   			for(int j = 0; j < 256; j++)
   			{
    		//
   				letter = (char) j;      
   				
   				//dla kazdego i-tego bajtu bloku sprawdzamy czy nasza litera j da cos logicznego po xorze
   				for(int i = 0; i < 256; i++)
    			{
    				
   					//sprawdzamy czy na tej pozycji nie ma juz ustalonego klucza. jezeli jest, to pomijamy te iteracje
    				if(!goodBytes[i])
    				{
    					//przeksztalcamy litere na bajt zeby moc swobodnie ja xorowac
    					byte byteLetter = (byte) letter;
    					
    					//xorujemy ja z xorem dwoch blokow
   						int temp = tempBytes[z][i] ^ byteLetter;
    					
   						//jezeli jest dobra to sprawdzamy czy Mi = j czy tez Mi+d =j
   						if(goodByte((byte) temp))
   						{
    						//inicjalizujemy dwoch kandydatow na klucz
   							int temp4 = encryptedBytes[z][i] ^ temp; 
    						int temp5 = encryptedBytes[z+1][i] ^ temp; 
    		
   			//				int temp4 = encryptedBytes[z][i] ^ byteLetter; 
   			//	    		int temp5 = encryptedBytes[z+1][i] ^ byteLetter; 
   							
    						//cast na bajty
    						byte temp4b = (byte) temp4;
    						byte temp5b = (byte) temp5;
    					
    						//Jezeli cala kolumna blokow na i-tej pozycji jest "dobra" to zaznaczamy te pozycje jako true w tblicy goodBytes i dopisujemy do klucza
    						if(goodColumn(encryptedBytes, temp4b, i))
   							{   			
   							goodBytes[i] = true;
   							keyBytes[i] = temp4b;
    						}
   							else if(goodColumn(encryptedBytes, temp5b, i))
   							{
   								goodBytes[i] = true;
   								keyBytes[i] = temp5b;
//   							int temp2 = encryptedBytes[0][i] ^ keyBytes[i];
//    							if(goodByte((byte) temp2))
//    	    					resultBytes[i] = (byte) temp2;
    						}
    					
    					}		
    				}
    			}				
    		}
    	}
    	
    	int counter = 0;
    	
		//Wypiujemy klucz, oznaczamy czy zosta� znaleziony i jego pozycj�
		for(int i = 0; i < 256; i++)
    	{
			
			System.out.print(i + ". ");
			
    		if(goodBytes[i])
    		{
    			System.out.print("jest   ");
    			counter++;
    		}
    		else
    			System.out.print("nie ma ");
    		
    		System.out.print(keyBytes[i] + "\n");
    	}
		
		
		//Liczba znalezionych bajt�w klucza
		System.out.print("Liczba dobrych: " + counter);
		
		System.out.print("\n\n");
		
		//Odszyfrowujemy tekst
		for(int i=0; i < blockSize - 1; i++)
			for(int j = 0; j < 256; j++)
			{
				int temp = encryptedBytes[i][j] ^ keyBytes[j];
				 decryptedBytes[i][j] = (byte) temp;
			}
		
		//Wypisujemy klucz
//		String str2 = new String(keyBytes);
//		char[] chr2 = str2.toCharArray();
//		System.out.print(chr2);

//			System.out.print(keyBytes[i] + " ");

		System.out.print("\n\n");
		
		//Wypisujemy tekst jawny - tylko "dobre" pozycje
    	for(int i = 0; i < blockSize; i++)
    	{
    	System.out.print("\n Blok " + i + ": \n");	
	//	String str3 = new String(decryptedBytes[i]);
    //	char[] chr3 = str3.toCharArray();
    //	System.out.print(chr3);
    	for(int j = 0; j <256; j++)
    	{
    		if(goodBytes[j])
    			System.out.print((char) decryptedBytes[i][j]);
    		else
    			System.out.print(" ");
    	}
    	
    	}
    	
    	System.out.print("\n");
    	
	    in.close();
	}

}
