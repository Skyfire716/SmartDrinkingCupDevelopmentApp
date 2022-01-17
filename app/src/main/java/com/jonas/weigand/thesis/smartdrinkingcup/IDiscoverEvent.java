package com.jonas.weigand.thesis.smartdrinkingcup;

public interface IDiscoverEvent {

    void communicationUpdate(CommunicationEnum type, String text);
}
