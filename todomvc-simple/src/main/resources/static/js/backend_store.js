/*jshint eqeqeq:false */
(function (window) {
  'use strict';

  /**
   * Creates a new client side storage object.
   *
   * @param {string} restUrl The restUrl of our DB we want to use
   * @param {function} callback Our fake DB uses callbacks because in
   * real life you probably would be making AJAX calls
   */
  function BackendStore(restUrl) {
    this._restApiUrl = restUrl;
    this.restApiFetchOptions = {
      headers: new Headers({
        "Accept": "application/json",
        "Content-type": "application/json"
      })
    };
  }

  /**
   * Finds items based on a query given as a JS object
   *
   * @param {object} query The query to match against (i.e. {foo: 'bar'})
   * @param {function} callback   The callback to fire when the query has
   * completed running
   *
   * @example
   * db.find({foo: 'bar', hello: 'world'}, function (data) {
	 *	 // data will return any items that have foo: bar and
	 *	 // hello: world in their properties
	 * });
   */
  BackendStore.prototype.find = function (query, callback) {
    if (!callback) {
      return;
    }

    var self = this;

    fetch(this._restApiUrl + "/search", {
      method: "POST",
      body: JSON.stringify(query),
      headers: this.restApiFetchOptions.headers
    }).then(function (resp) {
      return resp.json();
    }).then(function (jsonData) {
      callback.call(self, jsonData);
    });
  };

  /**
   * Will retrieve all data from the collection
   *
   * @param {function} callback The callback to fire upon retrieving data
   */
  BackendStore.prototype.findAll = function (callback) {
    callback = callback || function () {
    };

    var self = this;

    fetch(this._restApiUrl, this.restApiFetchOptions)
      .then(function (resp) {
        return resp.json();
      }).then(function (jsonData) {
      callback.call(self, jsonData);
    });
  };

  /**
   * Will save the given data to the DB. If no item exists it will create a new
   * item, otherwise it'll simply update an existing item's properties
   *
   * @param {object} updateData The data to save back into the DB
   * @param {function} callback The callback to fire after saving
   * @param {number} id An optional param to enter an ID of an item to update
   */
  BackendStore.prototype.save = function (updateData, callback, id) {

    callback = callback || function () {
    };

    var self = this;

    if (!id) {
      // create new todo

      fetch(this._restApiUrl, {
        method: "POST",
        body: JSON.stringify(updateData),
        headers: this.restApiFetchOptions.headers
      }).then(function (resp) {
        self.findAll(callback);
      });

      return;
    }

    // update existing todo

    fetch(this._restApiUrl + "/" + id, {
      method: "PUT",
      body: JSON.stringify(updateData),
      headers: this.restApiFetchOptions.headers
    }).then(function (resp) {
      self.findAll(callback);
    });
  };

  /**
   * Will remove an item from the Store based on its ID
   *
   * @param {number} id The ID of the item you want to remove
   * @param {function} callback The callback to fire after saving
   */
  BackendStore.prototype.remove = function (id, callback) {

    var self = this;

    fetch(this._restApiUrl + "/" + id, {
      method: "DELETE",
      headers: this.restApiFetchOptions.headers
    }).then(function (resp) {
      self.findAll(callback);
    });
  };

  /**
   * Will drop all storage and start fresh
   *
   * @param {function} callback The callback to fire after dropping the data
   */
  BackendStore.prototype.drop = function (callback) {
    //NOOP
  };

  // Export to window
  window.app = window.app || {};
  window.app.BackendStore = BackendStore;
})(window);
