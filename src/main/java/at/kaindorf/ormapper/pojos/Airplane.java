package at.kaindorf.ormapper.pojos;

import at.kaindorf.ormapper.annotations.Column;
import at.kaindorf.ormapper.annotations.Entity;
import at.kaindorf.ormapper.annotations.Id;

import java.time.LocalDate;

/**
 * Project: Exa_OR-Mapper_4CHIF
 * Created by: SF
 * Date: 01.03.2024
 * Time: 09:08
 */
@Entity(name = "airbus")
public class Airplane {

  @Id
  private Long airplaneId;
  @Column(length = 100, unique = true, nullable = false)
  private String name;

  private Double wingSpan;

  @Column(nullable = false)
  private int maxNumberOfPassengers;

  @Column(name = "build_at")
  private LocalDate buildDate;

}
