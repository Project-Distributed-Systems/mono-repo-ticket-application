package com.ticket.ticket.domain.event;

import java.util.UUID;
import com.ticket.ticket.infrastructure.json.IDDeserializer;

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = IDDeserializer.class)
public class ID {
  UUID identificator;

  public ID(UUID identificator) {
    this.identificator = identificator;
  }

  public UUID value() {
    return this.identificator;
  }
}
