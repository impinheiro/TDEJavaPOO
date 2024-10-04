import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.DataFormatException;

import dao.loan.DaoFactory;
import dao.loan.EmployeeDao;
import dao.loan.EquipmentDao;
import dao.loan.LoansDao;
import db.DB;
import db.DbException;
import loans.Employee;
import loans.Equipment;
import loans.Loans;


public class Main {


	public static void main(String[] args) {

		showMenu();

	}

	private static void showMenu() {
		Scanner scanner = new Scanner(System.in);
		EquipmentDao equipmentDao = DaoFactory.createEquipmentDao();
		EmployeeDao employeeDao = DaoFactory.createEmployeeDao();
		LoansDao loansDao = DaoFactory.createLoansDao();

		boolean continuar = true;
		do {

			System.out.println("==== Bem vindo! ====");
			System.out.println("1 - Registrar novo empréstimo");
			System.out.println("2 - Registar funcionário");
			System.out.println("3 - Registrar novo equipamento");
			System.out.println("4 - Ver emprestimos acontecendo");
			System.out.println("5 - Registrar retorno de equipamento");
			System.out.println("6 - Atualizar equipamento");
			System.out.println("7 - Mostrar historico do equipamento");
			System.out.println("8 - Listar equipamentos");
			System.out.println("9 - Listar funcionários");
			System.out.println("10 - Sair");

			int opcao = scanner.nextInt();
			scanner.nextLine();

			switch (opcao) {

			case 1:
				registerLoan();
				break;
			case 2:
				registerEmployee();
				break;
			case 3:
				registerEquipment();
				break;
			case 4:
				List<Loans> loans = loansDao.findAll();
				for (Loans obj : loans) {
					System.out.println(obj);
				}
				if (loans.isEmpty()) {
					System.out.println("Nenhum emprestimo registrado");
					System.out.println("=============================");
				}
				break;
			case 5:
				updateLoan();
				break;
			case 6:
				updateEquipment();
				break;
			case 7:
				showHistoryById();
				break;
			case 8:
				showEquipments();
				break;
			case 9:
				showEmployees();
				break;
			case 10:

				continuar = false;
				break;
			default:
				System.out.println("Opção inválida! Selecione novamente");
				break;
			}

		} while (continuar);
		scanner.close();
		System.out.println("Programa terminado!");
	}

	private static void showEmployees() {
		// INSTANCIA UM GERENCIADOR DE ACESSO A EMPREGADOS NO BANCO DE DADOS
		EmployeeDao employeeDao = DaoFactory.createEmployeeDao();
		// UMA LISTA PARA RECEBER OS EMPREGADOS LISTADOS NO BANCO DE DADOS
		List<Employee> employees = employeeDao.findAll();
		for (Employee employee : employees) {
			System.out.println(employee);
		}

	}

	private static void showEquipments() {
		// INSTANCIA UM GERENCIADOR DE ACESSO A EQUIPAMENTOS NO BANCO DE DADOS
		EquipmentDao equipmentDao = DaoFactory.createEquipmentDao();
		// LISTA QUE RECEBE OS EQUIPAMENTOS LISTADOS NO BANCO DE DADOS
		List<Equipment> equipments = equipmentDao.findAll();
		for (Equipment equipment : equipments) {
			System.out.println(equipment);
		}

	}

	private static void showHistoryById() {
		Scanner scanner = new Scanner(System.in);
		// GERENCIADOR DE ACESSO A OBJETO
		EquipmentDao equipmentDao = DaoFactory.createEquipmentDao();
		System.out.println("Qual ID do equipamento voce deseja visualizar o historico?");
		// MOSTRA OS EQUIPAMENTOS PARA QUE O USUARIO ESCOLHA QUAL HISTORICO DE
		// EQUIPAMENTO SERA EXIBIDO
		List<Equipment> allEquipment = equipmentDao.findAll();
		for (Equipment obj : allEquipment) {
			System.out.println("ID: " + obj.getId() + " Nome: " + obj.getDescription());
		}
		System.out.println("=======================================");
		System.out.println("ID: ");
		Integer id = scanner.nextInt();
		scanner.nextLine();
		// OBJETO EQUIPAMENTO RECEBE O DADO QUE FOI ENCONTRADO PELO GERENCIADOR DE
		// OBJETO
		Equipment equipment = equipmentDao.findById(id);
		System.out.println("Historico de manutenção: " + equipment.getHistory());

	}

	private static void registerLoan() {
		Scanner scanner = new Scanner(System.in);
		// INSTANCIA GERENCIADOR DE ACESSO A OBJETOS DE EQUIPAMENTO, EMPRESTIMO E
		// EMPREGADOS
		LoansDao loansDao = DaoFactory.createLoansDao();
		EmployeeDao employeeDao = DaoFactory.createEmployeeDao();
		EquipmentDao equipmentDao = DaoFactory.createEquipmentDao();
		// TRATAMENTO DE EXCEÇÃO, POIS ELE PODE RETORNAR UM PROBLEMA DE CONVERSAO DE
		// DATA
		try {

			System.out.println("ID do equipamento a ser retirado: ");
			List<Equipment> listEquipments = equipmentDao.findAll();
			for (Equipment obj : listEquipments) {
				System.out.println(obj);
			}
			Integer equipmentId = scanner.nextInt();
			scanner.nextLine();
			// LISTA PARA RECEBER TODOS OS EMPRESTIMOS EXISTENTES NO BANCO DE DADOS
			List<Loans> activeLoans = new ArrayList<>();
			activeLoans = loansDao.findAll();
			Equipment equipmentLoan = equipmentDao.findById(equipmentId);
			// INSTANCIAÇÃO DE STRINGS COM O OBJETIVO DE FAZER VERIFICAÇÃO BOOLEANA COM O
			// ESTADO DE CONSERVAÇÃO DOS OBJETOS
			String bad = "RUIM";
			String damaged = "DANIFICADO";
			// VERIFICA SE HA ALGUM EMPRESTIMO NO BANCO
			// PRIMEIRO PASSO DO METODO É PARA VERIFICAR O ESTADO DO EQUIPAMENTO SE ESTA
			// EMPRESTADO OU COM PROBLEMAS
			if (activeLoans.isEmpty()) {
				// ESTADO DO EQUIPAMENTO
				if (equipmentLoan.getConditionStatus().equals(bad)
						|| equipmentLoan.getConditionStatus().equals(damaged)) {
					System.out.println("Equipamento atualmente indisponível");
					return;
				} else {
					System.out.println("Por favor informe a data e hora de retirada respectivamente: ");
					String checkout = scanner.nextLine();

					System.out.println("Observações: ");
					String notes = scanner.nextLine();

					System.out.println("ID do funcionario realizando a retirada: ");
					//MOSTRA TODOS OS EMPREGADOS
					List<Employee> listEmployees = employeeDao.findAll();
					for(Employee obj : listEmployees) {
						System.out.println(listEmployees);	
					}
					Integer employeeId = scanner.nextInt();
					scanner.nextLine();
					Employee employeeLoan = employeeDao.findById(employeeId);

					Loans loan = new Loans(null, checkout, employeeLoan, equipmentLoan, notes);
					loansDao.insert(loan);
				}
				//SE HOUVER ALGUM EMPRESTIMO REGISTRADO PERCORRE A LISTA E COMPARA SE O ID DO EQUIP PASSADO É IGUAL A ALGUM 
				//QUE JA EXISTE NA LISTA
			} else {
				for (Loans obj : activeLoans) {
					if (obj.getEquipment().getId().equals(equipmentLoan.getId())) {
						System.out.println("EQUIPAMENTO EMPRESTADO!");
						return;
					} else if (equipmentLoan.getConditionStatus().equals(bad)
							|| equipmentLoan.getConditionStatus().equals(damaged)) {
						System.out.println("Equipamento atualmente indisponível");
						return;
					}
				}
			}

		} catch (ParseException e) {	
			throw new DbException(e.getMessage());
		}
	}

	private static void registerEmployee() {
		EmployeeDao employeeDao = DaoFactory.createEmployeeDao();
		Scanner scanner = new Scanner(System.in);
		try {
			
			System.out.println("Qual o nome do empregado?");
			String name = scanner.nextLine();
			System.out.println("Qual a função que o empregado desempenha?");
			String role = scanner.nextLine();
			System.out.println("Quando o empregado foi admitido?");
			System.out.println("Por favor passar em formato de dia/mes/ano ");
			System.out.println("Exemplo: dd/MM/aaaa");
			String date = scanner.next();
			Employee employee = new Employee(null, name, role, date);
			//GERENCIADOR DE ACESSO A OBJETO USA O METODO DE INSERÇÃO NO BANCO DE DADOS
			employeeDao.insert(employee);
			System.out.println(employee);

		} catch (ParseException e) {
			throw new DbException(e.getMessage());
		}
	}

	private static void registerEquipment() {
		Scanner scanner = new Scanner(System.in);
		EquipmentDao equipmentDao = DaoFactory.createEquipmentDao();
		try {

			System.out.println("Descrição do equipamento: ");
			String description = scanner.nextLine();
			System.out.println("Data de aquisição: dd/MM/aaaa");
			String purchaseDate = scanner.nextLine();
			System.out.println("Peso: ");
			double weight = scanner.nextDouble();
			System.out.println("Largura: ");
			double width = scanner.nextDouble();
			System.out.println("Comprimento: ");
			double length = scanner.nextDouble();
			System.out.println("Condição do equipamento: ");
			System.out.println("1: BOM " + "2: RUIM " + "3: DANIFICADO " + "4: NOVO");
			int option = scanner.nextInt();
			scanner.nextLine();
			String condition_status = null;
			boolean continuar = true;
			//LOOP QUE SERA EXECUTADO FORÇANDO AO USUARIO INFORMAR O ESTADO DO EQUIPAMENTO
			while (continuar)
				switch (option) {
				case 1:
					condition_status = "BOM";
					continuar = false;
					break;
				case 2:
					condition_status = "RUIM";
					continuar = false;
					break;
				case 3:
					condition_status = "DANIFICADO";
					continuar = false;
					break;
				case 4:
					condition_status = "NOVO";
					continuar = false;
					break;
				default:
					System.out.println("Escolha uma opção válida.");
					break;
				}
			String history = String.join("/", "Recentemente adicionado");
			//CONVERTE A STRING EM ARRAY C O OBJETIVO DE MANTER O HISTORICO NO BANCO DE DADOS TAMBÉM
			ArrayList<String> historyArray = new ArrayList<>(Arrays.asList(history.split("/")));
			Equipment equipment = new Equipment(null, description, purchaseDate, weight, width, length, historyArray,
					condition_status);
			//OBJETO DE ACESSO A DADOS INSERE O EQUIPAMENTO NO BANCO
			equipmentDao.insert(equipment);
			System.out.println(equipment);
		} catch (ParseException e) {
			throw new DbException(e.getMessage());
		}
	}

	private static void updateLoan() {
		//OBJETO DE ACESSO A EMPRESTIMOS INSTANCIADO
		LoansDao loansDao = DaoFactory.createLoansDao();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Qual o ID do emprestimo feito?");
		Integer id = scanner.nextInt();
		scanner.nextLine();
		//OBJETO DE EMPRESTIMO RECEBE O EMPRESTIMO QUE O DAO ENCONTRA POR COMPARAÇÃO DE ID NO BANCO DE DADOS
		Loans loan1 = loansDao.findById(id);
		System.out.println("Qual a data de retorno?");
		System.out.println("Data e hora no formato: dd/MM/aaaa HH:mm");
		String date = scanner.nextLine();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		//SETA A DATA DE RETORNO DO EQUIPAMENTO E CONVERTE A STRING EM SEU TIPO LOCALDATETIME 
		loan1.setReturnTime(LocalDateTime.parse(date, dtf));
		//METODO QUE ATUALIZA O DADO NO BANCO DE DADOS
		loansDao.update(loan1);
	}

	private static void updateEquipment() {
		EquipmentDao equipmentDao = DaoFactory.createEquipmentDao();
		Scanner scanner = new Scanner(System.in);
		System.out.println("ID do Equipamento a ser atualizado: ");
		Integer id = scanner.nextInt();
		scanner.nextLine();
		//RECEBE O EQUIPAMENTO A SER ATUALIZADO
		Equipment equipment = equipmentDao.findById(id);
		System.out.println("Condição do equipamento: ");
		System.out.println("1: BOM " + "2: RUIM " + "3: DANIFICADO " + "4: NOVO");
		int option = scanner.nextInt();
		scanner.nextLine();
		String condition_status = null;
		boolean continuar = true;
		while (continuar)
			switch (option) {
			case 1:
				condition_status = "BOM";
				continuar = false;
				break;
			case 2:
				condition_status = "RUIM";
				continuar = false;
				break;
			case 3:
				condition_status = "DANIFICADO";
				continuar = false;
				break;
			case 4:
				condition_status = "NOVO";
				continuar = false;
				break;
			default:
				System.out.println("Escolha uma opção válida.");
				break;
			}
		System.out.println("Historico de manutenção: ");
		String maintenance = scanner.nextLine();
		//RECEBE O HISTORICO DE MANUTENÇÃO E ADICIONA A ATUALIZAÇÃO A LISTA
		ArrayList<String> update = equipment.getHistory();
		update.add(maintenance);
		//MUDA O ESTADO DE CONSERVAÇÃO E O HISTORICO E PASSA NOVAMENTE PRO BANCO
		equipment.setConditionStatus(condition_status);
		equipment.setHistory(update);
		equipmentDao.update(equipment);
	}

}
//LoansDao loanDao = DaoFactory.createLoansDao();
//EquipmentDao equipDao = DaoFactory.createEquipmentDao();
//
//boolean check = loanDao.isEquipmentAvailable(equipDao.findById(2).getId());
//System.out.println(check);

//List<Equipment> list = equipmentDao.findAll();		
//
//for(Equipment obj : list) {
//	System.out.println(obj);
//}

//Equipment equipment = new Equipment(null, "Cama", new Date(2023-12-12), 30.0, 30.0, 30.0,"Cama ha um tempo sem vender, mas nenhum problema apresentado", "Bem conservado");
//equipmentDao.insert(equipment);
//System.out.println("Inserido! Novo Id: "+ equipment.getId());
/*
 * Equipment equipment = new Equipment(); equipment = equipmentDao.findById(4);
 * equipment.setHistory("Cama consertada"); equipment.setConditionStatus("Bom");
 * equipmentDao.update(equipment);
 */

//Loans loan = new Loans();
//loan = loansDao.findById(1);
//System.out.println(loan);

//try {
//	Employee employee = new Employee(null, "Teste23", "teste34", "2013-09-11");
//	employeeDao.insert(employee);
//	System.out.println(employee);
//} catch (ParseException e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//}

//employee = employeeDao.findById(11);
//employee.setRole("xerecudo");
//employeeDao.update(employee);

//
//Equipment obj = new Equipment();
//obj = equipmentDao.findById(1);
//System.out.println(obj);
//launch(args);
//EquipmentDao equipmentDao = DaoFactory.createEquipmentDao();
//@Override
//public void start(Stage stage) {
//	try {
//		Parent parent = FXMLLoader.load(getClass().getResource("/gui/View.fxml"));
//		Scene scene = new Scene(parent);
//		stage.setScene(scene);
//		stage.show();
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
//
//}