import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Spring Data сам поймет, что вы хотите найти мероприятия по ID организатора
    List<Event> findByOrganizerId(Long organizerId);

    // ... (здесь можно будет добавить методы для фильтрации [cite: 141])
}