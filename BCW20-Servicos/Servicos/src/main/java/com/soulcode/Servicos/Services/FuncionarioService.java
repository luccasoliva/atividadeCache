package com.soulcode.Servicos.Services;

import com.soulcode.Servicos.Models.Cargo;
import com.soulcode.Servicos.Models.Funcionario;
import com.soulcode.Servicos.Repositories.CargoRepository;
import com.soulcode.Servicos.Repositories.FuncionarioRepository;
import com.soulcode.Servicos.Services.Exceptions.DataIntegrityViolationException;
import com.soulcode.Servicos.Services.Exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// quando se fala em serviços, estamos falando dos métodos do crud da tabela

@Service
public class FuncionarioService {

    // aqui se faz a injeção de dependência
    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    CargoRepository cargoRepository;

    @Cacheable("funcionariosCache")
     public List<Funcionario> mostrarTodosFuncionarios(){

        return funcionarioRepository.findAll();
    }



    @Cacheable(value = "funcionariosCache", key = "#idFuncionario")
    public Funcionario mostrarUmFuncionarioPeloId(Integer idFuncionario)
    {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(idFuncionario);
        return funcionario.orElseThrow(
                () -> new EntityNotFoundException("Funcionário não cadastrado: " + idFuncionario)
        );
    }

    @Cacheable(value = "funcionariosCache", key = "#email")
    public Funcionario mostrarUmFuncionarioPeloEmail(String email){
        Optional<Funcionario> funcionario = funcionarioRepository.findByEmail(email);
        return funcionario.orElseThrow();
    }

    @Cacheable(value = "funcionariosCache", key = "#idCargo")
    public List<Funcionario> mostrarTodosFuncionariosDeUmCargo(Integer idCargo){
        Optional<Cargo> cargo = cargoRepository.findById(idCargo);
        return funcionarioRepository.findByCargo(cargo);
    }

    @CachePut(value = "funcionariosCache", key = "#funcionario.idFuncionario")
    public Funcionario cadastrarFuncionario(Funcionario funcionario, Integer idCargo) throws DataIntegrityViolationException {
        //só por precaução nós vamos colocar o id do funcionário como nullo
        funcionario.setIdFuncionario(null);
        Optional<Cargo> cargo = cargoRepository.findById(idCargo);
        funcionario.setCargo(cargo.get());
        return funcionarioRepository.save(funcionario);
    }

    @CacheEvict(value = "funcionariosCache", key = "#idFuncionario")
    public void excluirFuncionario(Integer idFuncionario){
        //mostrarUmFuncionarioPeloId(idFuncionario);
        funcionarioRepository.deleteById(idFuncionario);
    }
    @CachePut(value = "funcionariosCache", key = "#funcionario.idFuncionario")
    public Funcionario editarFuncionario(Funcionario funcionario){
        return funcionarioRepository.save(funcionario);
    }

    @Cacheable(value = "funcionariosCache", key = "#idFuncionario")
    public Funcionario salvarFoto(Integer idFuncionario, String caminhoFoto){
        Funcionario funcionario = mostrarUmFuncionarioPeloId(idFuncionario);
        funcionario.setFoto(caminhoFoto);
        return funcionarioRepository.save(funcionario);
    }
}
