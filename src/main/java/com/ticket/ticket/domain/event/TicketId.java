package com.ticket.ticket.domain.event;

import java.util.UUID;

public class TicketId {
  ID id;

  public TicketId(ID ticket_id) {
    this.id = ticket_id;
  }

  public TicketId(UUID id) {
    this.id = new ID(id);
  }

  public UUID value() {
    return this.id.identificator;
  }
}
