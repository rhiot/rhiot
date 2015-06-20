describe('Geofencing utils', function () {
    it('Should mock location href.', function () {
        var location = 'someLocation';
        Geofencing.windowLocationHref = function(){return location};
        expect(Geofencing.windowLocationHref()).toBe(location);
    });
});