package com.soulcode.Servicos.Services;

import com.soulcode.Servicos.Models.Chamado;
import com.soulcode.Servicos.Models.Cliente;
import com.soulcode.Servicos.Models.Funcionario;
import com.soulcode.Servicos.Models.StatusChamado;
import com.soulcode.Servicos.Repositories.ChamadoRepository;
import com.soulcode.Servicos.Repositories.ClienteRepository;
import com.soulcode.Servicos.Repositories.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ChamadoService {

    @Autowired
    ChamadoRepository chamadoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Cacheable("chamadoCache")
    public List<Chamado> mostrarTodosChamados(){
        return chamadoRepository.findAll();	}

    @Cacheable(value = "chamadoCache", key = "#idChamado")
    public Chamado mostrarUmChamado(Integer idChamado) {
        Optional<Chamado> chamado = chamadoRepository.findById(idChamado);
        return chamado.orElseThrow();
    }
    @Cacheable(value = "chamadoCache", key = "#idCliente")
    public List<Chamado> buscarChamadosPeloCliente(Integer idCliente){
        Optional<Cliente> cliente = clienteRepository.findById(idCliente);
        return chamadoRepository.findByCliente(cliente);

    }
    @Cacheable(value = "chamadoCache", key = "#idFuncionario")
    public List<Chamado> buscarChamadosPeloFuncionario(Integer idFuncionario){
        Optional<Funcionario> funcionario = funcionarioRepository.findById(idFuncionario);
        return chamadoRepository.findByFuncionario(funcionario);
    }
    @Cacheable(value = "chamadoCache", key = "#status")
    public List<Chamado> buscarChamadosPeloStatus(String status){
        return chamadoRepository.findByStatus(status);
    }

    @Cacheable(value = "chamadoCache", key = "#data1")
    public List<Chamado> buscarPorIntervaloData(Date data1, Date data2){
        return chamadoRepository.findByIntervaloData(data1,data2);
    }

    @CachePut(value = "chamadoCache", key = "#idCliente")
    public Chamado cadastrarChamado(Chamado chamado, Integer idCliente){
        // regra 3 - atribuuição do status recebido pra o chamado que está sendo cadastrado
        chamado.setStatus(StatusChamado.RECEBIDO);
        // regra 2 - dizer que ainda não atribuimos esse chamado pra nenhum funcionário
        chamado.setFuncionario(null);
        //regra 1 - buscando os dados do cliente dono do chamado
        Optional<Cliente> cliente = clienteRepository.findById(idCliente);
        chamado.setCliente(cliente.get());
        return chamadoRepository.save(chamado);
    }

    @CacheEvict(value = "chamadoCache", key = "idChamado", allEntries = true)
    public void excluirChamado(Integer idChamado){
        chamadoRepository.deleteById(idChamado);
    }

    @CacheEvict(value = "chamadoCache", key = "idChamado", allEntries = true)
    public Chamado editarChamado(Chamado chamado, Integer idChamado){
        //instanciamos aqui um objeto do tipo Chamado para guardar os dados do chamados
        //sem as novas alteracoes
        Chamado chamadoSemAsNovasAlteracoes = mostrarUmChamado(idChamado);
        Funcionario funcionario = chamadoSemAsNovasAlteracoes.getFuncionario();
        Cliente cliente = chamadoSemAsNovasAlteracoes.getCliente();

        chamado.setCliente(cliente);
        chamado.setFuncionario(funcionario);
        return chamadoRepository.save(chamado);
    }

    @CachePut(value = "chamadoCache", key = "idFuncionario")
    public Chamado atribuirFuncionario(Integer idChamado, Integer idFuncionario){
        // buscar os dados do funcionário que vai ser atibuído a esse chamado
        Optional<Funcionario> funcionario = funcionarioRepository.findById(idFuncionario);
        // buscar o chamado para o qual vai ser especificado o funcionário escolhido
        Chamado chamado = mostrarUmChamado(idChamado);
        chamado.setFuncionario(funcionario.get());
        chamado.setStatus(StatusChamado.ATRIBUIDO);

        return chamadoRepository.save(chamado);
    }

    @CachePut(value = "chamadoCache", key = "#idChamado")
    public Chamado modificarStatus(Integer idChamado,String status){
        Chamado chamado = mostrarUmChamado(idChamado);
        switch (status){
            case "ATRIBUIDO":
            {
                chamado.setStatus(StatusChamado.ATRIBUIDO);
                break;
            }
            case "CONCLUIDO":
            {
                chamado.setStatus(StatusChamado.CONCLUIDO);
                break;
            }
            case "ARQUIVADO":
            {
                chamado.setStatus(StatusChamado.ARQUIVADO);
                break;
            }
            case "RECEBIDO":
            {
                chamado.setStatus(StatusChamado.RECEBIDO);
                break;
            }
        }
        return chamadoRepository.save(chamado);
    }


}
