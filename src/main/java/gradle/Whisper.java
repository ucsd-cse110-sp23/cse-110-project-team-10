package gradle;

import java.io.*;
import java.net.*;
import org.json.*;

public class Whisper {
	 private static final String API_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
	 private static final String TOKEN = "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";
	 private static final String MODEL = "whisper-1";
	 private static final String FILE_PATH = "/Users/jierubai/Downloads/Lab4Audio.mp3";
	 
	 public Whisper() {
		 
	 }
	// Helper method to write a parameter to the output stream in multipart form data format 
	 private static void writeParameterToOutputStream(
		 OutputStream outputStream,
		 String parameterName,
		 String parameterValue,
		 String boundary
	 ) throws IOException {
		 outputStream.write(("--" + boundary + "\r\n").getBytes());
		 outputStream.write(
		 (
			"Content-Disposition: form-data; name=\"" + parameterName + "\"\r\n\r\n"
		 ).getBytes()
	     );
		 outputStream.write((parameterValue + "\r\n").getBytes());
	 }
	 
	// Helper method to write a file to the output stream in multipart form data format 
	 private static void writeFileToOutputStream(
		OutputStream outputStream,
		File file,
		String boundary
	 ) throws IOException {
		outputStream.write(("--" + boundary + "\r\n").getBytes());
		outputStream.write(
		(
			"Content-Disposition: form-data; name=\"file\"; filename=\"" +
			file.getName() +
			"\"\r\n"
		).getBytes()
		);
		outputStream.write(("Content-Type: audio/mpeg\r\n\r\n").getBytes());
		
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = fileInputStream.read(buffer)) != -1) {
			 outputStream.write(buffer,0,bytesRead);
		}
		fileInputStream.close();
	 }
	 
	// Helper method to handle a successful response
	 private static String handleSuccessResponse(HttpURLConnection connection) throws IOException,JSONException {
		 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		 String inputLine;
		 StringBuilder response = new StringBuilder(); 
		 while ((inputLine = in.readLine()) != null) { 
			 response.append(inputLine);
		 }
		 in.close();
		 JSONObject responseJson = new JSONObject(response.toString());
		 String generatedText = responseJson.getString("text");
		 // Print the transcription result
		 System.out.println("Transcription Result: " + generatedText);
		 return generatedText;
	}
	 
	private static void handleErrorResponse(HttpURLConnection connection) throws IOException,JSONException {
		BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
		String errorLine;
		StringBuilder errorResponse = new StringBuilder();
		while((errorLine = errorReader.readLine()) != null) {
			errorResponse.append(errorLine);
		}
		errorReader.close();
		String errorResult = errorResponse.toString();
		System.out.println("Error Result: " + errorResult);
	}
	
	public String transcribe(String filename) throws IOException {
		File file = new File(filename);
        URI uri = URI.create(API_ENDPOINT);
		URL url = uri.toURL();
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		String boundary = "Boundary-" + System.currentTimeMillis();
		connection.setRequestProperty(
				"Content-Type",
		        "multipart/form-data; boundary=" + boundary
		);
		connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
		OutputStream outputStream = connection.getOutputStream();
		writeParameterToOutputStream(outputStream, "model", MODEL, boundary);
		writeFileToOutputStream(outputStream, file, boundary);
		outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
		outputStream.flush();
		outputStream.close();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return handleSuccessResponse(connection);
        } else {
            handleErrorResponse(connection);
            return null;
        }
    }
	
	public static void main(String[] args) throws IOException {
		File file = new File(FILE_PATH);
		URI uri = URI.create(API_ENDPOINT);
		URL url = uri.toURL();
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		String boundary = "Boundary-" + System.currentTimeMillis();
		connection.setRequestProperty(
				"Content-Type",
		        "multipart/form-data; boundary=" + boundary
		);
		connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
		OutputStream outputStream = connection.getOutputStream();
		writeParameterToOutputStream(outputStream, "model", MODEL, boundary);
		writeFileToOutputStream(outputStream, file, boundary);
		outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
		outputStream.flush();
		outputStream.close();
		int responseCode = connection.getResponseCode();
		if(responseCode == HttpURLConnection.HTTP_OK) {
			handleSuccessResponse(connection);
		} else {
			handleErrorResponse(connection);
		}
		 connection.disconnect();
	}
}
