import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { 
  LayoutDashboard, 
  MessageSquare, 
  Users, 
  FileText, 
  Bell, 
  Settings, 
  Search,
  PlusCircle,
  TrendingUp,
  Image as ImageIcon,
  LogOut,
  Heart,
  Share2
} from 'lucide-react';

const API_BASE = "http://localhost:8080/api/v1";

const Login = ({ onLogin }) => {
  const [isSignup, setIsSignup] = useState(false);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleAction = async () => {
    if (!username || !password || (isSignup && !email)) return;
    setIsLoading(true);
    setError('');
    setSuccess('');
    try {
      if (isSignup) {
        await axios.post(`${API_BASE}/users/auth/signup`, { username, email, password });
        setSuccess('Account created! Please sign in.');
        setIsSignup(false);
      } else {
        const response = await axios.post(`${API_BASE}/users/auth/signin`, { username, password });
        onLogin(response.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Action failed. Check backend status.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="h-screen w-full bg-gray-950 flex items-center justify-center p-4">
      <div className="bg-gray-900 border border-gray-800 p-8 rounded-3xl w-full max-w-md shadow-2xl space-y-8 animate-in fade-in slide-in-from-bottom-8 duration-500">
        <div className="text-center space-y-2">
          <h1 className="text-4xl font-black bg-gradient-to-r from-blue-400 to-emerald-400 bg-clip-text text-transparent">ARENA</h1>
          <p className="text-gray-400">{isSignup ? 'Create your account' : 'Welcome back, Developer'}</p>
        </div>
        <div className="space-y-4">
          {error && <div className="p-3 bg-red-500/10 border border-red-500/30 rounded-xl text-red-500 text-sm text-center">{error}</div>}
          {success && <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-xl text-emerald-500 text-sm text-center">{success}</div>}
          <input 
            type="text" 
            value={username} 
            onChange={(e) => setUsername(e.target.value)}
            className="w-full bg-gray-800 border-none rounded-xl p-4 focus:ring-2 focus:ring-blue-500 outline-none text-white" 
            placeholder="Username" 
          />
          {isSignup && (
            <input 
              type="email" 
              value={email} 
              onChange={(e) => setEmail(e.target.value)}
              className="w-full bg-gray-800 border-none rounded-xl p-4 focus:ring-2 focus:ring-blue-500 outline-none text-white" 
              placeholder="Email Address" 
            />
          )}
          <input 
            type="password" 
            value={password} 
            onChange={(e) => setPassword(e.target.value)}
            className="w-full bg-gray-800 border-none rounded-xl p-4 focus:ring-2 focus:ring-blue-500 outline-none text-white" 
            placeholder="Password" 
          />
          <button 
            onClick={handleAction}
            disabled={isLoading}
            className="w-full bg-blue-600 hover:bg-blue-500 py-4 rounded-xl font-bold transition-all shadow-lg shadow-blue-900/40 text-white disabled:opacity-50"
          >
            {isLoading ? 'Processing...' : (isSignup ? 'Sign Up' : 'Sign In')}
          </button>
        </div>
        <div className="text-center">
          <button 
            onClick={() => setIsSignup(!isSignup)}
            className="text-sm text-gray-500 hover:text-blue-400 transition-colors"
          >
            {isSignup ? 'Already have an account? Sign In' : 'Need an account? Sign Up'}
          </button>
        </div>
      </div>
    </div>
  );
};

const Dashboard = ({ currentUser }) => {
  const [stats, setStats] = useState({ totalPosts: 0, totalFollows: 0 });
  const [feed, setFeed] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('dashboard');
  const [isCreatePostOpen, setIsCreatePostOpen] = useState(false);
  const [postContent, setPostContent] = useState('');
  const [selectedImage, setSelectedImage] = useState(null);
  const [selectedChat, setSelectedChat] = useState(null);
  const [chatMessages, setChatMessages] = useState(() => {
    const saved = localStorage.getItem('arena_chats');
    return saved ? JSON.parse(saved) : {};
  });
  const [currentMessage, setCurrentMessage] = useState('');
  const [following, setFollowing] = useState([]);
  const fileInputRef = useRef(null);

  const token = localStorage.getItem('arena_token');
  const userId = currentUser.id;

  const fetchData = async () => {
    setIsLoading(true);
    try {
      const [feedRes, statsRes, followingRes] = await Promise.all([
        axios.get(`${API_BASE}/posts/feed?userId=${userId}`, { headers: { Authorization: `Bearer ${token}` } }),
        axios.get(`${API_BASE}/analytics/stats`, { headers: { Authorization: `Bearer ${token}` } }),
        axios.get(`${API_BASE}/users/social/following/${userId}`, { headers: { Authorization: `Bearer ${token}` } })
      ]);
      setFeed(feedRes.data);
      setStats({
        totalPosts: statsRes.data.totalPosts || 0,
        totalFollows: statsRes.data.totalFollows || 0
      });
      setFollowing(followingRes.data);
    } catch (err) {
      console.error("Error fetching dashboard data:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [activeTab]);

  useEffect(() => {
    localStorage.setItem('arena_chats', JSON.stringify(chatMessages));
  }, [chatMessages]);

  const handleLogout = () => {
    localStorage.removeItem('arena_token');
    window.location.reload();
  };

  const handleLike = (id) => {
    setFeed(feed.map(post => 
      post.id === id ? { ...post, likes: (post.likes || 0) + 1, isLiked: true } : post
    ));
  };

  const handlePostSubmit = async () => {
    if (!postContent.trim() && !selectedImage) return;
    try {
      await axios.post(`${API_BASE}/posts?userId=${userId}`, {
        userId: userId,
        username: currentUser.username,
        content: postContent,
        image: selectedImage
      }, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setPostContent('');
      setSelectedImage(null);
      setIsCreatePostOpen(false);
      fetchData(); // Refresh feed
    } catch (err) {
      console.error("Error creating post:", err);
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => setSelectedImage(reader.result);
      reader.readAsDataURL(file);
    }
  };

  const handleFollow = async (id) => {
    try {
      if (following.includes(id)) {
        await axios.post(`${API_BASE}/users/social/unfollow/${id}?userId=${userId}`, {}, {
          headers: { Authorization: `Bearer ${token}` }
        });
      } else {
        await axios.post(`${API_BASE}/users/social/follow/${id}?userId=${userId}`, {}, {
          headers: { Authorization: `Bearer ${token}` }
        });
      }
      fetchData(); // Refresh following and stats
    } catch (err) {
      console.error("Error toggling follow:", err);
    }
  };

  const handleAddMediaClick = () => {
    fileInputRef.current?.click();
  };

  const getConvKey = (id1, id2) => {
    const clean = (id) => id.toString().replace('user', '').trim();
    return [clean(id1), clean(id2)].sort().join('-');
  };

  const handleSendMessage = () => {
    if (!currentMessage.trim() || !selectedChat) return;
    const msg = {
      id: Date.now(),
      senderId: currentUser.id,
      receiverId: selectedChat.toString(),
      text: currentMessage,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };
    
    const convKey = getConvKey(currentUser.id, selectedChat);

    setChatMessages({
      ...chatMessages,
      [convKey]: [...(chatMessages[convKey] || []), msg]
    });
    setCurrentMessage('');
  };

  return (
    <div className="flex h-screen bg-gray-950 text-white overflow-hidden font-sans">
      {/* Sidebar */}
      <aside className="w-64 bg-gray-900 border-r border-gray-800 flex flex-col">
        <div className="p-6">
          <h1 className="text-2xl font-bold bg-gradient-to-r from-blue-400 to-emerald-400 bg-clip-text text-transparent">
            ARENA
          </h1>
        </div>
        
        <nav className="flex-1 px-4 space-y-2 mt-4">
          <NavItem icon={<LayoutDashboard size={20} />} label="Dashboard" active={activeTab === 'dashboard'} onClick={() => setActiveTab('dashboard')} />
          <NavItem icon={<FileText size={20} />} label="Feed" active={activeTab === 'feed'} onClick={() => setActiveTab('feed')} />
          <NavItem icon={<MessageSquare size={20} />} label="Messages" active={activeTab === 'messages'} onClick={() => setActiveTab('messages')} />
          <NavItem icon={<Users size={20} />} label="Social Graph" active={activeTab === 'social'} onClick={() => setActiveTab('social')} />
          <NavItem icon={<ImageIcon size={20} />} label="Media Library" active={activeTab === 'media'} onClick={() => setActiveTab('media')} />
        </nav>

        <div className="p-4 border-t border-gray-800 space-y-4">
          <div className="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-800 transition-colors cursor-pointer">
            <div className="w-10 h-10 rounded-full bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center font-bold text-sm">
              {currentUser.username?.substring(0, 2).toUpperCase()}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold truncate">{currentUser.username}</p>
              <p className="text-xs text-gray-400">Arena Member</p>
            </div>
          </div>
          
          <button 
            onClick={handleLogout}
            className="w-full flex items-center gap-2 px-4 py-2 text-sm text-red-400 hover:bg-red-500/10 rounded-lg transition-all border border-transparent hover:border-red-500/30"
          >
            <LogOut size={16} />
            Logout
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col overflow-hidden">
        {/* Header */}
        <header className="h-16 bg-gray-900/50 backdrop-blur-md border-b border-gray-800 flex items-center justify-between px-8">
          <div className="relative w-96">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500" size={18} />
            <input 
              type="text" 
              placeholder="Search platform..." 
              className="w-full bg-gray-800 border-none rounded-full py-2 pl-10 pr-4 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
            />
          </div>
          
          <div className="flex items-center gap-6">
            <button 
              onClick={() => setIsCreatePostOpen(true)}
              className="bg-blue-600 hover:bg-blue-500 text-white px-4 py-2 rounded-full text-sm font-semibold flex items-center gap-2 transition-all shadow-lg shadow-blue-900/20"
            >
              <PlusCircle size={18} />
              Create Post
            </button>
            <div className="relative cursor-pointer">
              <Bell className="text-gray-400 hover:text-white transition-colors" size={22} />
              <span className="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full text-[10px] flex items-center justify-center font-bold">3</span>
            </div>
            <Settings className="text-gray-400 hover:text-white cursor-pointer transition-colors" size={22} />
          </div>
        </header>

        {/* Scrollable Area */}
        <section className="flex-1 overflow-y-auto p-8 space-y-8">
          {activeTab === 'dashboard' && (
            <>
              {/* Hero Section */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <StatCard label="Total Posts" value={stats.totalPosts} icon={<FileText className="text-blue-400" />} trend="+12% this week" />
                <StatCard label="Platform Follows" value={stats.totalFollows} icon={<Users className="text-emerald-400" />} trend="+5.2k new" />
                <StatCard label="Engagement" value="4.8/5" icon={<TrendingUp className="text-purple-400" />} trend="Top 1% service" />
              </div>

              {/* Dynamic Content */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* Feed Section */}
                <div className="bg-gray-900 rounded-2xl p-6 border border-gray-800">
                  <h3 className="text-lg font-bold mb-6 flex items-center gap-2">
                    <FileText className="text-blue-500" size={20} />
                    Recent Feed
                  </h3>
                  <div className="space-y-6">
                    {feed.map(post => (
                      <PostItem key={post.id} post={post} onLike={handleLike} />
                    ))}
                  </div>
                </div>

                {/* Quick Actions / Activity */}
                <div className="space-y-8">
                  <div className="bg-gradient-to-br from-gray-900 to-blue-950 rounded-2xl p-6 border border-blue-900/30">
                    <h3 className="text-lg font-bold mb-4">System Status</h3>
                    <div className="space-y-3">
                      <StatusRow label="API Gateway" status="Healthy" />
                      <StatusRow label="Eureka Discovery" status="Healthy" />
                      <StatusRow label="Post Service" status="Healthy" color="text-emerald-500" />
                      <StatusRow label="Config Server" status="Healthy" />
                    </div>
                  </div>

                  <div className="bg-gray-900 rounded-2xl p-6 border border-gray-800">
                    <h3 className="text-lg font-bold mb-4">Quick Upload</h3>
                    <div className="border-2 border-dashed border-gray-800 rounded-xl p-8 flex flex-col items-center justify-center text-gray-500 hover:border-blue-500 hover:bg-blue-500/5 transition-all cursor-pointer group">
                      <ImageIcon size={40} className="mb-4 group-hover:text-blue-500 transition-colors" />
                      <p className="text-sm font-medium">Drag and drop images here</p>
                      <p className="text-xs mt-1">Supports JPG, PNG up to 10MB</p>
                    </div>
                  </div>
                </div>
              </div>
            </>
          )}

          {activeTab === 'feed' && (
            <div className="max-w-2xl mx-auto space-y-6">
              <h2 className="text-2xl font-bold mb-8">Personalized Feed</h2>
              {feed.map(post => (
                <div key={post.id} className="bg-gray-900 p-6 rounded-2xl border border-gray-800">
                  <PostItem post={post} onLike={handleLike} />
                </div>
              ))}
            </div>
          )}

          {activeTab === 'media' && (
            <div className="space-y-8">
              <h2 className="text-2xl font-bold">Media Library</h2>
              <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                {feed.filter(p => p.image).map(post => (
                  <div key={post.id} className="aspect-square rounded-2xl overflow-hidden border border-gray-800 hover:border-blue-500 transition-all cursor-pointer group">
                    <img src={post.image} className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" />
                  </div>
                ))}
                <div className="aspect-square rounded-2xl border-2 border-dashed border-gray-800 flex flex-col items-center justify-center text-gray-600 hover:border-blue-500 hover:text-blue-500 transition-all cursor-pointer">
                  <PlusCircle size={32} />
                  <span className="text-xs mt-2 font-bold">Upload New</span>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'messages' && (
            <div className="h-[calc(100vh-8rem)] flex bg-gray-900 rounded-3xl overflow-hidden border border-gray-800 animate-in fade-in zoom-in duration-300">
              {/* Chat Sidebar */}
              <div className="w-80 border-r border-gray-800 flex flex-col">
                <div className="p-6 border-b border-gray-800">
                  <h3 className="text-xl font-bold">Messages</h3>
                </div>
                <div className="flex-1 overflow-y-auto">
                  {[101, 102, 103, 104, 105].filter(id => id !== userId).map(id => (
                    <div 
                      key={id}
                      onClick={() => setSelectedChat(id)}
                      className={`p-4 flex items-center gap-4 cursor-pointer transition-colors ${selectedChat === id ? 'bg-blue-600/10 border-r-2 border-blue-500' : 'hover:bg-gray-800'}`}
                    >
                      <div className="w-12 h-12 rounded-full bg-gray-800 flex items-center justify-center font-bold text-blue-400">U{id % 10}</div>
                      <div className="flex-1 min-w-0">
                        <p className="font-bold text-sm">User {id}</p>
                        <p className="text-xs text-gray-500 truncate">Hey, how's the platform going?</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Chat Window */}
              {selectedChat ? (
                <div className="flex-1 flex flex-col bg-gray-950/50">
                  <div className="p-4 border-b border-gray-800 flex items-center gap-4 bg-gray-900/50">
                    <div className="w-10 h-10 rounded-full bg-blue-500/20 flex items-center justify-center font-bold text-blue-400">
                      {selectedChat.toString().includes('user') || isNaN(selectedChat) 
                        ? selectedChat.toString().substring(0, 2).toUpperCase() 
                        : `U${selectedChat % 10}`}
                    </div>
                    <h4 className="font-bold">{isNaN(selectedChat) ? selectedChat : `User ${selectedChat}`}</h4>
                  </div>
                  
                  <div className="flex-1 overflow-y-auto p-6 space-y-4">
                    {(() => {
                      const convKey = getConvKey(currentUser.id, selectedChat);
                      return (chatMessages[convKey] || []).map(msg => (
                        <div key={msg.id} className={`flex ${msg.senderId === currentUser.id ? 'justify-end' : 'justify-start'}`}>
                          <div className={`max-w-[70%] p-4 rounded-2xl text-sm ${msg.senderId === currentUser.id ? 'bg-blue-600 text-white rounded-tr-none' : 'bg-gray-800 text-gray-200 rounded-tl-none'}`}>
                            {msg.text}
                            <p className="text-[10px] mt-1 opacity-50 text-right">{msg.time}</p>
                          </div>
                        </div>
                      ));
                    })()}
                  </div>

                  <div className="p-6 border-t border-gray-800 flex gap-4">
                    <input 
                      type="text" 
                      value={currentMessage}
                      onChange={(e) => setCurrentMessage(e.target.value)}
                      onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
                      placeholder="Type a message..." 
                      className="flex-1 bg-gray-800 border-none rounded-xl px-4 py-3 focus:ring-2 focus:ring-blue-500 outline-none"
                    />
                    <button 
                      onClick={handleSendMessage}
                      className="bg-blue-600 hover:bg-blue-500 p-3 rounded-xl transition-all"
                    >
                      <Share2 size={20} className="rotate-90" />
                    </button>
                  </div>
                </div>
              ) : (
                <div className="flex-1 flex flex-col items-center justify-center text-gray-500 gap-4">
                  <MessageSquare size={64} className="opacity-20" />
                  <p className="text-xl font-medium">Select a user to start chatting</p>
                </div>
              )}
            </div>
          )}

          {activeTab === 'social' && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[101, 102, 103, 104, 105, 106].map(id => (
                <div key={id} className="bg-gray-900 p-6 rounded-2xl border border-gray-800 flex items-center justify-between hover:border-blue-500/50 transition-all group">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 rounded-full bg-blue-500/10 flex items-center justify-center text-blue-400 font-bold group-hover:bg-blue-500 group-hover:text-white transition-all">U{id % 10}</div>
                    <div>
                      <p className="font-bold">User {id}</p>
                      <p className="text-xs text-gray-500">{following.includes(id) ? 'Mutual connection' : 'Suggested for you'}</p>
                    </div>
                  </div>
                  <button 
                    onClick={() => handleFollow(id)}
                    className={`px-6 py-2 rounded-full text-xs font-bold transition-all ${
                      following.includes(id) 
                      ? 'bg-emerald-500/10 text-emerald-500 border border-emerald-500/50 hover:bg-red-500/10 hover:text-red-500 hover:border-red-500/50' 
                      : 'bg-blue-600 text-white shadow-lg shadow-blue-900/40 hover:bg-blue-500'
                    }`}
                  >
                    {following.includes(id) ? 'Following' : 'Follow'}
                  </button>
                </div>
              ))}
            </div>
          )}
        </section>
      </main>

      {/* Create Post Modal */}
      {isCreatePostOpen && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-gray-900 border border-gray-800 w-full max-w-lg rounded-2xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-200">
            <div className="p-6 border-b border-gray-800 flex items-center justify-between">
              <h3 className="text-xl font-bold">Create New Post</h3>
              <button onClick={() => setIsCreatePostOpen(false)} className="text-gray-400 hover:text-white transition-colors">
                <PlusCircle size={24} className="rotate-45" />
              </button>
            </div>
            <div className="p-6 space-y-4">
              <input 
                type="file" 
                ref={fileInputRef} 
                className="hidden" 
                onChange={handleImageChange}
              />
              <textarea 
                value={postContent}
                onChange={(e) => setPostContent(e.target.value)}
                placeholder="What's on your mind?" 
                className="w-full bg-gray-800 border-none rounded-xl p-4 min-h-[150px] resize-none focus:ring-2 focus:ring-blue-500 outline-none text-lg"
              />
              {selectedImage && (
                <div className="relative rounded-xl overflow-hidden border border-gray-800">
                  <img src={selectedImage} alt="Preview" className="w-full h-32 object-cover" />
                  <button 
                    onClick={() => setSelectedImage(null)}
                    className="absolute top-2 right-2 bg-black/50 p-1 rounded-full hover:bg-black/70"
                  >
                    <PlusCircle size={20} className="rotate-45" />
                  </button>
                </div>
              )}
              <div className="flex items-center justify-between">
                <button 
                  onClick={handleAddMediaClick}
                  className="flex items-center gap-2 text-blue-400 hover:text-blue-300 font-medium transition-colors"
                >
                  <ImageIcon size={20} />
                  Add Media
                </button>
                <button 
                  onClick={handlePostSubmit}
                  className="bg-blue-600 hover:bg-blue-500 text-white px-8 py-2 rounded-full font-bold shadow-lg shadow-blue-900/40 transition-all"
                >
                  Post
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const NavItem = ({ icon, label, active, onClick }) => (
  <div 
    onClick={onClick}
    className={`flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-all ${
      active ? 'bg-blue-600 text-white' : 'text-gray-400 hover:bg-gray-800 hover:text-white'
    }`}
  >
    {icon}
    <span className="text-sm font-medium">{label}</span>
  </div>
);

const StatCard = ({ label, value, icon, trend }) => (
  <div className="bg-gray-900 p-6 rounded-2xl border border-gray-800 hover:border-gray-700 transition-all">
    <div className="flex items-center justify-between mb-4">
      <div className="p-3 bg-gray-800 rounded-xl">{icon}</div>
      <span className="text-xs font-bold text-emerald-400">{trend}</span>
    </div>
    <p className="text-sm text-gray-400 font-medium">{label}</p>
    <p className="text-3xl font-bold mt-1">{value}</p>
  </div>
);

const PostItem = ({ post, onLike }) => (
  <div className="flex gap-4 p-4 rounded-xl hover:bg-gray-800/50 transition-colors border border-transparent hover:border-gray-800">
    <div className="w-12 h-12 rounded-full bg-gray-700 flex-shrink-0 flex items-center justify-center font-bold">
      {post.username ? post.username.substring(0, 2).toUpperCase() : `U${post.userId % 10}`}
    </div>
    <div className="flex-1 min-w-0">
      <div className="flex items-center justify-between mb-1">
        <h4 className="text-sm font-bold truncate">{post.username || `User ${post.userId}`}</h4>
        <span className="text-xs text-gray-500">{new Date(post.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
      </div>
      <p className="text-sm text-gray-300 leading-relaxed">{post.content}</p>
      
      {post.image && (
        <div className="mt-3 rounded-xl overflow-hidden border border-gray-800">
          <img src={post.image} alt="Post content" className="w-full h-auto max-h-96 object-cover" />
        </div>
      )}

      <div className="flex items-center gap-6 mt-4">
        <button 
          onClick={() => onLike(post.id)}
          className={`text-xs flex items-center gap-1.5 transition-colors ${post.isLiked ? 'text-red-500' : 'text-gray-500 hover:text-red-500'}`}
        >
          <Heart size={14} fill={post.isLiked ? "currentColor" : "none"} />
          {post.likes || 0}
        </button>
        <button className="text-xs text-gray-500 hover:text-blue-400 flex items-center gap-1.5 transition-colors">
          <MessageSquare size={14} />
          Comment
        </button>
        <button className="text-xs text-gray-500 hover:text-emerald-400 flex items-center gap-1.5 transition-colors">
          <Share2 size={14} />
          Share
        </button>
      </div>
    </div>
  </div>
);

const StatusRow = ({ label, status, color = "text-emerald-500" }) => (
  <div className="flex items-center justify-between text-sm py-1">
    <span className="text-gray-400">{label}</span>
    <span className={`font-bold ${color}`}>{status}</span>
  </div>
);

const App = () => {
  const [userData, setUserData] = useState(() => {
    const saved = localStorage.getItem('arena_user_data');
    return saved ? JSON.parse(saved) : null;
  });

  const handleLogin = (data) => {
    localStorage.setItem('arena_token', data.token);
    localStorage.setItem('arena_user_data', JSON.stringify(data));
    setUserData(data);
  };

  if (!userData) {
    return <Login onLogin={handleLogin} />;
  }

  return <Dashboard currentUser={userData} />;
};

export default App;
