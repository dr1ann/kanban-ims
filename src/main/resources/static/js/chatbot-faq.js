    const chatbot = document.getElementById("chatbot");
    function toggleChatbot() {
        chatbot.classList.toggle("hidden");
        chatbot.classList.toggle('flex');
    }
  
  function sendMessage(message, sender = "bot") {
    const chatWindow = document.getElementById("chat-window");
  
    // Create a container for the message
    const messageDiv = document.createElement("div");
    messageDiv.classList.add("flex", "items-start", "space-x-2");
    if (sender === "user") {
      messageDiv.classList.add("justify-end"); // Add class for user alignment
    }
  
    // Avatar for user or bot
    const avatar = document.createElement("div");
    avatar.innerHTML = sender === "user" ? '<i class="fa-solid fa-user text-xs p-[2px]"></i>' : '<i class="fa-solid fa-robot text-xs p-[2px]"></i>';
    avatar.className = sender === "user"
      ? "w-8 h-8 bg-gray-400 rounded-full flex items-center justify-center text-white font-bold"
      : "w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center text-white font-bold";
  
    // Message bubble
    const bubble = document.createElement("div");
    bubble.textContent = message;
    bubble.className = sender === "user"
      ? "bg-gray-200 text-gray-800 p-3 rounded-lg max-w-xs"
      : "bg-blue-100 text-gray-800 p-3 rounded-lg max-w-xs";
  
    // Append avatar and bubble to message container
    if (sender === "user") {
      messageDiv.appendChild(bubble);
      messageDiv.appendChild(avatar);
    } else {
      messageDiv.appendChild(avatar);
      messageDiv.appendChild(bubble);
    }
  
    // Append message container to chat window
    chatWindow.appendChild(messageDiv);
    chatWindow.scrollTop = chatWindow.scrollHeight; // Auto-scroll to the latest message
  }
  
  
  // Function to handle FAQ button clicks
  function faqClick(question, answer) {
    sendMessage(question, "user"); // Show user's question
    setTimeout(() => sendMessage(answer, "bot"), 500); // Simulate bot delay and reply
  }

  const chatbotButton = document.getElementById('chatbotButton');

  window.addEventListener('scroll', () => {
    // Calculate the position of the scroll
    const scrollPosition = window.scrollY + window.innerHeight;
    const documentHeight = document.documentElement.scrollHeight;
  
    // Check if the user is at the bottom
    if (scrollPosition >= documentHeight) {
      chatbotButton.style.display = 'none';
      chatbot.classList.add('hidden');
      chatbot.classList.remove('flex');
    } else {
      chatbotButton.style.display = 'block';
    }
  });
  
