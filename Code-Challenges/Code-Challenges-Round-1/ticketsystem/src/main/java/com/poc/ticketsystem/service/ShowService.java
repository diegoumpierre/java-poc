package com.poc.ticketsystem.service;

import com.poc.ticketsystem.model.Show;
import com.poc.ticketsystem.model.User;

public interface ShowService {


    boolean buyTicket(User user, Show show);


}
