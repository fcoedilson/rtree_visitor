package br.edu.fa7.service;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.edu.fa7.entity.Area;


@Repository
@Transactional
public class AreaService extends BaseService<Integer, Area>{

}
