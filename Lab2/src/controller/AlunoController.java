package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import listeners.Cadastrar;
import listeners.Deletar;
import presentation.Atualizar;
import presentation.Cadastro;
import presentation.Listar;
import presentation.Tela;

public class AlunoController {
	private String filepath = "db.csv";
	private File db;
	private Tela tela;
	private Cadastrar cadastroListener;
	private listeners.Atualizar atualizaListener;
	private Deletar deleteListener;
	
	public AlunoController(Tela tela) {
		db = new File(filepath);
		this.tela = tela;
		
		if(!db.exists()) {
			try {
				db.createNewFile();
				FileWriter fw = new FileWriter(this.db);
				fw.append("Nome, CPF, Matrícula, Vertente\n");
				fw.close();
			} catch (Exception e) {
				System.out.println("Não é possível criar banco de dados.");
			}
		}
		
	}
	
	public boolean cadastrarAluno(Aluno aluno) {
		String nome = aluno.getNome(); 
		String cpf = aluno.getCpf();
		String matricula = aluno.getMatricula();
		String vertente = aluno.getVertente();

		if(nome.isBlank() || cpf.isBlank() || vertente.isBlank())
			return false;
		String textodb = nome + ", " + cpf + ", " + matricula + ", " + vertente+"\n";
		try {
			FileWriter fw = new FileWriter(this.db,true);
			fw.append(textodb);
			fw.close();
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	public Object[][] listarAlunos(){
		ArrayList<String> linhas = new ArrayList<String>();
		
		try {
			BufferedReader fr = new BufferedReader(new FileReader(this.db));
			boolean fimDoArquivo = false;
			fr.readLine(); //pula primeira linha
			
			int nLinhas = 0;
			String linha = "";
			while(!fimDoArquivo) {
				linha = fr.readLine();
				if(linha != null) {
					linhas.add(linha);
					nLinhas++;
				}else {
					fimDoArquivo = true;
				}
				
			}
			fr.close();
			
			Object[][] alunoTabela = new Object[linhas.size()][4];
			
			for (int i = 0; i < linhas.size(); i++) {
				String[] tokens = linhas.get(i).split(", ");
				if(tokens.length == 4) {
					alunoTabela[i][0] = tokens[0];
					alunoTabela[i][1] = tokens[1];
					alunoTabela[i][2] = tokens[2];
					alunoTabela[i][3] = tokens[3];
				}
			}
			return alunoTabela;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	};
	public boolean atualizarAluno(String matricula, String vertente) {
		int nLinha = alunoExiste(matricula);
		if(nLinha != -1) {
			try {
				String tmp = "tmp_db.csv";
				File dbTemp = new File(tmp);
				
				BufferedReader frDB = new BufferedReader(new FileReader(this.db));
				FileWriter fwTemp  = new FileWriter(dbTemp,true);
				
				String linha = "";
				int iLinha = 0;
				boolean fimArquivo = false;
				while(!fimArquivo) {
					linha = frDB.readLine();
					iLinha++;	
					if(linha != null) {
						if(iLinha == nLinha) {
							String[] tokens  = linha.split(", ");
							tokens[3] = vertente;
							fwTemp.append(String.join(", ", tokens)+'\n');
						}else {
							fwTemp.append(linha+'\n');
						}
					}else {
						fimArquivo = true;
					}
					
				}
				fwTemp.close();
				frDB.close();
				dbTemp.renameTo(this.db);
				
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean deletarAluno(Aluno aluno) {
		
		int linha = alunoExiste(aluno.getMatricula());
		if(linha != -1) {
			return removerLinha(linha);
		}
		return false;
		
	}
	
	private boolean removerLinha(int nLinha) {
		try {
			String tmp = "tmp_db.csv";
			File dbTemp = new File(tmp);
			
			BufferedReader frDB = new BufferedReader(new FileReader(this.db));
			FileWriter fwTemp  = new FileWriter(dbTemp,true);
			
			String linha = "";
			int iLinha = 0;
			boolean fimArquivo = false;
			while(!fimArquivo) {
				linha = frDB.readLine();
				iLinha++;	
				if(linha != null) {
					if(iLinha != nLinha) 
						fwTemp.append(linha+'\n');
				}else {
					fimArquivo = true;
				}
				
			}
			fwTemp.close();
			frDB.close();
			dbTemp.renameTo(this.db);
			this.db = dbTemp;
			
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	//retorna a linha ou -1 se não achar
	public int alunoExiste(String matricula) {
		try {
			BufferedReader fr = new BufferedReader(new FileReader(this.db));
			fr.readLine();
			
			int linhaNumero = 0;
			String linha = "";
			while(linha != null) {
				linha = fr.readLine();
				linhaNumero++;
				String [] tokens = linha.split(", ");
				if(tokens[2].trim().compareTo(matricula.trim()) == 0) {
					return linhaNumero+1;
				}
			}
			
			fr.close();
		} catch (Exception e) {
			return -1;
		}
		return -1;
	}

	public Cadastrar getCadastroListener() {
		return cadastroListener;
	}

	public void setCadastroListener(Cadastrar cadastroListener) {
		this.cadastroListener = cadastroListener;
	}

	public listeners.Atualizar getAtualizaListener() {
		return atualizaListener;
	}

	public void setAtualizaListener(listeners.Atualizar atualizaListener) {
		this.atualizaListener = atualizaListener;
	}

	public Deletar getDeleteListener() {
		return deleteListener;
	}

	public void setDeleteListener(Deletar deleteListener) {
		this.deleteListener = deleteListener;
	}
	
	public Tela getTela() {
		return tela;
	}

	public void mostrarCadastro() {
		tela.setDisplay(new Cadastro(this.cadastroListener));
	}
	
	public void mostrarAtualiza() {
		tela.setDisplay(new Atualizar(this.atualizaListener));
	}
}
