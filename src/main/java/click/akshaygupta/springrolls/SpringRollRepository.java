package click.akshaygupta.springrolls;

import org.springframework.data.repository.CrudRepository;

public interface SpringRollRepository extends CrudRepository<SpringRoll, Long> {

    SpringRoll findByIdAndOwner(Long id, String owner);
    
}
