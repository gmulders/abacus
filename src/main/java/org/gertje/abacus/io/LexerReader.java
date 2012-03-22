package org.gertje.abacus.io;

import java.io.IOException;
import java.io.Reader;

/**
 * LexerReader is een Reader met de volgende eigenschappen:
 * - de reader kan bufferen
 * - de reader kan voor uit spieken
 * - de reader houdt regelnummers en kolomnummers bij
 * - de reader ondersteund markering
 * - de reader kan NIET een heel blok tekens ineens inlezen of tekens overslaan.
 */
public class LexerReader extends Reader {

	/**
	 * De reader die we 'decoraten' met deze reader. (Zie het decorating pattern.)
	 */
    private Reader in;

    /**
     * De buffer waarin we de gegevens bufferen.
     */
    private char buffer[];
    
    /**
     * Het laatst gelezen teken.
     */
    private int previousChar;
    
    /**
     * Het nummer van de kolom van het huidige teken.
     */
    private int columnNumber;
    
    /**
     * Het nummer van de regel van het huidige teken.
     */
	private int lineNumber;
	
	/**
	 * De index van de buffer, wijst naar het volgende teken wat gelezen gaat worden.
	 */
	private int bufferIndex;
	
	/**
	 * Het aantal tekens in de buffer, geteld vanaf het begin van de buffer.
	 */
	private int numberOfCharactersInBuffer;

	/**
	 * De index van de markering.
	 */
	private int markedIndex;

	/**
	 * Het kolomnummer wat hoort bij de gemarkeerde index.
	 */
	private int markedColumnNumber;

	/**
	 * Het regelnummer wat hoort bij de gemarkeerde index.
	 */
	private int markedLineNumber;

	/**
	 * De standaard buffer grootte.
	 */
	private static int DEFAULT_BUFFER_SIZE = 2048;

	/**
	 * Constructor.
	 * 
	 * @param in Reader die we decoraten.
	 * @param bufferSize De grootte van de buffer.
	 */
	public LexerReader(Reader in, int bufferSize) {
		super(in);
		if (bufferSize <= 0) {
			throw new IllegalArgumentException("bufferSize <= 0");
		}		
		this.in = in;
		buffer = new char[bufferSize];
		bufferIndex = numberOfCharactersInBuffer = 0;
		previousChar = -1;
		markedIndex = -1;
		lineNumber = 1;
		columnNumber = 1;
	}

	/**
	 * Constructor. Roept de andere constructor aan met de default buffer grootte.
	 * @param in
	 */
	public LexerReader(Reader in) {
		this(in, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Controleert of de input stream nog open is, wanneer dit niet het geval is gooit de methode een exceptie.
	 * @throws IOException
	 */
	public void checkInputStream() throws IOException {
		// Wanneer de input stream niet open is gooien we een exceptie.
		if (in == null) {
			throw new IOException("Closed input stream.");
		}
	}

	/**
	 * Sluit de reader en zorgt ervoor dat geheugen geruimd kan worden door de garbage collector.
	 * @throws IOException
	 */
	public void close() throws IOException {
		synchronized (lock) {
			// Wanneer de gewrapte reader al null is hoeven we verder niets te doen.
			if (in == null) {
				return;
			}
			// Sluit de gewrapte reader en maak variabalen null zodat het geheugen geruimd kan worden.
			in.close();
			in = null;
			buffer = null;
		}
	}

	/**
	 * Ververst de buffer. Dient alleen aangeroepen te worden wanneer de buffer leeg is.
	 * @return Het aantal bijgelezen tekens.
	 * @throws IOException Een exceptie wanneer de buffer vol zit.
	 */
	private int refreshBuffer() throws IOException {
		// We onderscheiden 4 situaties:
		// - er is nog ruimte na het laatste teken vrij,
		// - er is geen markering actief,
		// - er is een markering actief en de index hiervan is 0 en 
		// - er is een markering actief en de index is niet 0.
		if (buffer.length > numberOfCharactersInBuffer) {
			// Er is nog ruimte na het laatste teken vrij, lees in.
			int n = in.read(buffer, bufferIndex, buffer.length - bufferIndex);
			numberOfCharactersInBuffer += n;
			return n;
		} else if (markedIndex < 0) {
			// Er is geen markering actief.
			numberOfCharactersInBuffer = in.read(buffer, 0, buffer.length);
			// Zet de index terug naar 0.
			bufferIndex = 0;
			return numberOfCharactersInBuffer;
		} else if (markedIndex == 0) {
			// Er is een markering actief en de index is 0.
			// Voorlopig kunnen we af met het gooien van een error.
			throw new IOException("Buffer full, marked index out of range.");

			// Een andere optie is de buffer vergroten en bij lezen. We verhogen de index met de oorspronkelijke 
			// bufferSize. Voor nu zet ik dit in commentaar:
			/*
			// Vergroot de buffer, maak hiervoor een nieuwe buffer aan.
			char newBuffer[] = new char[buffer.length + bufferSize];
			// Verplaats de inhoud van de buffer naar de nieuwe buffer.
			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
			// Lees extra karakters bij.
			int n = in.read(newBuffer, buffer.length, bufferSize);
			// Laat de pointer van de buffer naar de nieuwe buffer wijzen.
			buffer = newBuffer;
			// Verhoog het aantal karakters in de buffer met het gelezen aantal.
			numberOfCharactersInBuffer += n;
			// De bufferIndex en de markedIndex blijven hetzelfde.

			// Geef het aantal gelezen karakters terug.
			return n;
			*/
		} else {
			// Er is een markering actief en de index is groter dan 0.
			// Kopieer eerst het einde van de array, vanaf de gemarkeerde index, naar het begin van de array.
			System.arraycopy(buffer, markedIndex, buffer, 0, numberOfCharactersInBuffer - markedIndex);

			// Lees karakters bij. Begin te lezen bij het einde van de vorige read en lees de resterende array vol.
			int n = in.read(buffer, numberOfCharactersInBuffer - markedIndex, buffer.length - (numberOfCharactersInBuffer - markedIndex));

			// Het aantal karakters in de buffer is het vorige aantal - het aantal karakters dat naar links in geschoven
			// plus het aantal gelezen karakters.
			numberOfCharactersInBuffer += -markedIndex + n;
			// De bufferIndex wordt verlaagd met de gemarkeerde index.
			bufferIndex -= markedIndex;
			// De gemarkeerde index wordt 0.
			markedIndex = 0;

			return n;
		}
	}

	@Override
	public int read() throws IOException {
		synchronized (lock) {
			checkInputStream();
			// Wanneer de index groter of gelijk is aan het aantal karakters in de buffer verversen we de buffer.
			if (bufferIndex >= numberOfCharactersInBuffer) {
				// Probeer de buffer te verversen, wanneer dit niet lukt geven we -1 terug.
				if (refreshBuffer() == -1) {
					return -1;
				}
			}
			
			int c = buffer[bufferIndex++];
			columnNumber++;

			// Wanneer hetdetermineNextToken teken een \r is of een \n, die niet vooraf is gegaan door een \r, hogen we het regelnummer 
			// op.
			if (c == '\r' || (c == '\n' && previousChar != '\r')) {
				lineNumber++;
				columnNumber = 1;
			}

			// Verander het laatst gelezen karakter.
			previousChar = c;
			
			// Geef het karakter op de huidige index terug en hoog de index op.
			return c;
		}
	}

	/**
	 * Spiekt vooruit wat het volgende teken is.
	 * 
	 * @return Het volgende teken.
	 * @throws IOException
	 */
	public int peek() throws IOException {
		synchronized (lock) {
			checkInputStream();
			// Wanneer de index groter of gelijk is aan het aantal karakters in de buffer verversen we de buffer.
			if (bufferIndex >= numberOfCharactersInBuffer) {
				// Probeer de buffer te verversen, wanneer dit niet lukt geven we -1 terug.
				if (refreshBuffer() == -1) {
					return -1;
				}
			}

			// Geef het karakter op de huidige index terug en hoog de index NIET op.
			return buffer[bufferIndex];
		}
	}
	
	@Override
    public boolean ready() throws IOException {
        synchronized (lock) {
            checkInputStream();
            return (bufferIndex < numberOfCharactersInBuffer) || in.ready();
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        synchronized (lock) {
            checkInputStream();
            markedIndex = bufferIndex;
            markedColumnNumber = columnNumber;
            markedLineNumber = lineNumber;
        }
    }

    @Override
    public void reset() throws IOException {
        synchronized (lock) {
            checkInputStream();
            if (markedIndex < 0) {
                throw new IOException("Stream not marked");
            }
            bufferIndex = markedIndex;
            markedIndex = -1;
            columnNumber = markedColumnNumber;
            lineNumber = markedLineNumber;
        }
    }

    /**
     * Leest een heel aantal tekens ineens in een array. DEZE METHODE IS NIET GEIMPLEMENTEERD.
     * @return Het aantal gelezen tekens.
     * @throws IOException
     */
	public int read(char cbuf[], int off, int len) throws IOException {
		throw new IOException("read() method is not supported in LexerReader.");
	}

    /**
     * Slaat een aantal tekens over. DEZE METHODE IS NIET GEIMPLEMENTEERD.
     * @return Het aantal overgeslagen tekens.
     * @throws IOException
     */	
	public long skip(long n) throws IOException {
		throw new IOException("skip() method is not supported in LexerReader.");
    }


	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
}