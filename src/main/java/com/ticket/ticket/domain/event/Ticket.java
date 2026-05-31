package com.ticket.ticket.domain.event;

import java.time.LocalDate;

import com.ticket.ticket.domain.exceptions.DomainException;

public class Ticket {
  private TicketState ticket_situation;
  private TicketId id;
  private ID event_id;
  private ID client_id;

  public Ticket(TicketState state, TicketId ticketId, ID event_id, ID client_id) {
    this.ticket_situation = state;
    this.id = ticketId;
    this.event_id = event_id;
    this.client_id = client_id;
  }

  // domain/event/Ticket.java — adiciona os getters
  public TicketId getId() {
    return id;
  }

  public ID getEventId() {
    return event_id;
  }

  public ID getClientId() {
    return client_id;
  }

  public TicketState getSituation() {
    return ticket_situation;
  }

  public boolean is_used() {
    return this.ticket_situation != TicketState.ATIVO;
  }

  public void validate(LocalDate event_date) throws Exception {
    if (this.is_used()) {
      throw new DomainException("This ticket is alredy used");
    }
    if (LocalDate.now().isAfter(event_date)) {
      throw new DomainException("This ticket expired");
    }
    this.ticket_situation = TicketState.USADO;
  }

  public void cancel() {
    switch (ticket_situation) {
      case ATIVO:
        ticket_situation = TicketState.CANCELADO;
        break;

      case CANCELADO:
        // Probally the cancelation policy was activate by a dropout event (Need a log
        // WARNING)
        break;

      case USADO:
        // Insert a warning in this field (A event cannot be canceled after happened,
        // therefore they ticket)
        throw new DomainException.TicketAlreadyUsedException(id);

    }
  }
}
