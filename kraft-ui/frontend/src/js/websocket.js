//import SockJS from 'sockjs-client';
//import { Client } from '@stomp/stompjs';
//
//// Replace with your backend endpoint
//const socketUrl = '/ws'; // e.g., Spring Boot endpoint
//
//const stompClient = new Client({
//  brokerURL: null, // Required to be null for SockJS
//  connectHeaders: {
//    // Optional auth headers
//  },
//  webSocketFactory: () => new SockJS(socketUrl),
//  reconnectDelay: 5000,
//  debug: (str) => {
//    console.log('STOMP: ' + str);
//  },
//  onConnect: () => {
//    console.log('Connected to WebSocket');
//
//    // Example subscription
//    stompClient.subscribe('/topic/notifications', message => {
//      console.log('Received:', message.body);
//    });
//  },
//  onStompError: (frame) => {
//    console.error('Broker error: ', frame.headers['message']);
//    console.error('Details: ', frame.body);
//  }
//});
//
//stompClient.activate();


import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

document.addEventListener('DOMContentLoaded', () => {
  const socket = new SockJS('/ws');

  const stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    debug: (str) => console.log(str),
  });

  stompClient.onConnect = function (frame) {
    console.log("Connected: " + frame);

    const wsData = document.querySelector(".ws-data");

    stompClient.subscribe('/topic/analytics/', function (message) {
      const payload = JSON.parse(message.body);

      wsData.innerText = payload.someField || JSON.stringify(payload, null, 2);
    });
  };

  stompClient.onStompError = function (frame) {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
  };

  stompClient.activate(); // ✅ actually starts the connection
});
