// package src;
package server.src;

import java.io.*;
import java.net.*;
import org.json.*;

public class Whisper {

    private static final String API_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
    private static final String TOKEN = "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";
    private static final String MODEL = "whisper-1";
    private static String FILE_PATH = "";


    public Whisper() {	 
    }


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
                ).getBytes());
        outputStream.write((parameterValue + "\r\n").getBytes());
    }

    // Helper method to write a file to the output stream in multipart form data
    // format
    // Helper method to write a byte array to the output stream in multipart form data format
    private static void writeBytesToOutputStream(
            OutputStream outputStream,
            byte[] audioBytes,
            String boundary
        ) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(
                (
                "Content-Disposition: form-data; name=\"file\"; filename=\"audio.wav\"\r\n"
                ).getBytes());

        outputStream.write(("Content-Type: audio/mpeg\r\n\r\n").getBytes());
        outputStream.write(audioBytes);
    }

	public String transcribeBytes(byte[] audioBytes) throws IOException {
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
		writeBytesToOutputStream(outputStream, audioBytes, boundary);
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
	
	

// Helper method to handle a successful response
private static String handleSuccessResponse(HttpURLConnection connection)
        throws IOException{
    BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
    String inputLine;
    StringBuilder response = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
    }
    in.close();
    JSONObject responseJson = new JSONObject(response.toString());
    String generatedText = responseJson.getString("text");
    return generatedText;
}


    // Helper method to handle an error response
    private static void handleErrorResponse(HttpURLConnection connection)
            throws IOException{
        BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream()));

        String errorLine;
        StringBuilder errorResponse = new StringBuilder();
        while ((errorLine = errorReader.readLine()) != null) {
            errorResponse.append(errorLine);
        }
        errorReader.close();
        String errorResult = errorResponse.toString();
        System.out.println("Error Result: " + errorResult);
    }
}
