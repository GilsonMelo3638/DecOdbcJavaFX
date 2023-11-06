package gui.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Alerts {

	// Método para exibir um alerta genérico.
	public static void showAlert(String title, String header, String content, AlertType type) {
		Alert alert = new Alert(type);  // Cria uma instância de Alert com o tipo especificado.
		alert.setTitle(title);          // Define o título do alerta.
		alert.setHeaderText(header);    // Define o cabeçalho do alerta.
		alert.setContentText(content);   // Define o conteúdo do alerta.
		alert.show();                   // Exibe o alerta na interface gráfica.
	}
	
	// Método para exibir uma caixa de diálogo de confirmação.
	public static Optional<ButtonType> showConfirmation(String title, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);  // Cria uma instância de Alert de confirmação.
		alert.setTitle(title);                             // Define o título da caixa de diálogo.
		alert.setHeaderText(null);                        // Não há cabeçalho na caixa de diálogo de confirmação.
		alert.setContentText(content);                     // Define o conteúdo da caixa de diálogo.
		return alert.showAndWait();                        // Exibe a caixa de diálogo e aguarda a resposta do usuário.
	}
}